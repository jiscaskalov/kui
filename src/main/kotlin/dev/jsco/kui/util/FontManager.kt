package dev.jsco.kui.util

import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import dev.jsco.kui.Kui
import dev.jsco.kui.mixin.DrawContextAccessor
import me.x150.renderer.fontng.FTLibrary
import me.x150.renderer.fontng.Font
import me.x150.renderer.fontng.FontScalingRegistry
import me.x150.renderer.fontng.GlyphBuffer
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.gui.DrawContext
import net.minecraft.resource.ResourceManager
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import org.jetbrains.annotations.ApiStatus
import java.io.File
import java.io.InputStreamReader
import java.nio.file.Files
import kotlin.jvm.optionals.getOrNull

/**
 * Provides loading fonts from resource files,
 * shaping text into glyph buffers and caching them,
 * and rendering text onto the screen.
 */
@Environment(EnvType.CLIENT)
object FontManager {

    private lateinit var lib: FTLibrary
    private lateinit var resourceManager: ResourceManager
    private val fonts: MutableMap<String, Font> = mutableMapOf()
    private val buffers: MutableMap<Pair<Text, String>, GlyphBuffer> = mutableMapOf()

    /**
     * Initializes and registers all fonts declared in `kui-fonts.json` files found
     * under `assets/*modid*/font` across all resource namespaces.
     *
     * **Note:** This is an internal method and should not be called directly by external code.
     * It is automatically invoked by [dev.jsco.kui.mixin.ResourceReloadLoggerMixin.finish].
     */
    @ApiStatus.Internal
    fun init() {
        clean()
        lib = FTLibrary()
        resourceManager = Kui.client.resourceManager

        resourceManager.allNamespaces.forEach { namespace ->
            val file = resourceManager.getResource(Identifier.of(namespace, "kui-fonts.json")).getOrNull()
            if (file != null) {
                runCatching {
                    val listType = object : TypeToken<List<FontEntry>>() {}.type
                    val fonts: List<FontEntry> = Gson().fromJson(InputStreamReader(file.inputStream), listType)
                    registerFonts(namespace, fonts)
                }.onFailure { Kui.logger.error("Failed to load fonts from '$namespace': ${it.message}") }
            }
        }
    }

    /**
     * Closes all loaded [Font] instances, clears all cached [GlyphBuffer]s,
     * and removes temporary font files from the system's temp directory.
     */
    fun clean() {
        fonts.forEach { it.value.close() }
        buffers.forEach { it.value.clear() }
        fonts.clear(); buffers.clear()
        clearTempFonts()
    }

    /**
     * Registers a list of font entries under the specified namespace
     * into the internal map and the [FontScalingRegistry].
     *
     * **Note:** This is an internal method and should not be called directly by external code.
     * It is automatically invoked by [init].
     *
     * @param namespace The namespace associated with the font entries (e.g., "kui").
     * @param entries The list of [FontEntry] objects representing fonts to register.
     */
    @ApiStatus.Internal
    fun registerFonts(namespace: String, entries: List<FontEntry>) {
        entries.forEach { entry ->
            val id = Identifier.of(namespace, "font/${entry.file}")
            val file = extractFont(id) ?: throw IllegalStateException("Font file for '$id' could not be extracted.")
            val font = Font(lib, file.path, 0, entry.size)
            this.fonts.put(id.path.removeSuffix(".ttf"), font)
            Kui.logger.info("Registered font '$entry' from '$namespace'")
        }
        val fonts = this.fonts.values
        if (fonts.isNotEmpty()) FontScalingRegistry.register(fonts.first(), *fonts.drop(1).toTypedArray())
    }

    /**
     * Extracts a font resource from the resource manager and writes it to a temporary file.
     * The temporary file is automatically marked for deletion on JVM exit.
     *
     * @param id The [Identifier] pointing to the `.ttf` font file in resources (e.g., `"modid:font/arial.ttf"`).
     * @return A [File] pointing to the extracted temporary font file, or `null` if the resource could not be found or read.
     */
    fun extractFont(id: Identifier): File? {
        val resource = resourceManager.getResource(id).getOrNull() ?: return null
        return runCatching {
            val dir = File(System.getProperty("java.io.tmpdir"), "kui-fonts").apply { mkdirs() }
            val tempFile = Files.createTempFile(dir.toPath(), "font_", ".ttf").toFile().apply { deleteOnExit() }
            resource.inputStream.use { input -> tempFile.outputStream().use { output -> input.copyTo(output) } }
            tempFile
        }.onFailure { Kui.logger.error("Failed to extract font from '$id': ${it.message}") }.getOrNull()
    }

    /**
     * Shapes text into a [GlyphBuffer] using the specified font.
     *
     * **Note:** This should not be called every frame.
     * Instead, shape the text once and reuse the resulting buffer for [draw].
     *
     * @param text Text to be shaped.
     * @param font The name of the font to use (without file extension or path).
     * @return a shaped [GlyphBuffer] containing the text.
     * @throws IllegalStateException If the specified font is not registered.
     */
    fun shape(text: Text, font: String): GlyphBuffer {
        val buffer = GlyphBuffer()
        val fontRef: Font = fonts.getOrElse("font/$font") { throw IllegalStateException("Cannot find font '$font' in registry") }
        buffer.addText(fontRef, text, 0f, 0f)
        buffer.offsetToTopLeft()
        buffers.put(Pair(text, font), buffer)
        return buffer
    }

    /**
     * Draws the contents of a [GlyphBuffer] onto the screen using the given [DrawContext].
     *
     * @param buffer The buffer containing the glyphs to render, can be produced in [shape].
     * @param context Provides the rendering context and matrices.
     * @param x The x-coordinate on the screen where the text should be drawn.
     * @param y The y-coordinate on the screen where the text should be drawn.
     * @param scale The scale to draw the text with
     */
    fun draw(buffer: GlyphBuffer, context: DrawContext, x: Float, y: Float, scale: Float = 1f) {
        context.matrices.push()
        context.matrices.scale(scale, scale, scale)
        buffer.draw(
            (context as DrawContextAccessor).vcp,
            context.matrices,
            x / scale,
            y / scale
        )
        context.matrices.scale(1f, 1f, 1f)
        context.matrices.pop()
    }

    /**
     * Retrieves a cached [GlyphBuffer] associated with the given [text],
     * or creates and caches one using [text] and [font] if not present.
     *
     * @param text The [Text] to shape into a buffer.
     * @param font The name of the font to use when shaping the text.
     * @return A [GlyphBuffer] containing the shaped text.
     */
    fun getOrCreateBuffer(text: Text, font: String): GlyphBuffer {
        return buffers.getOrPut(Pair(text, font)) {
            shape(text, font)
        }
    }

    /**
     * Deletes all temporary `.ttf` font files\
     * stored in the `kui-fonts` directory inside the system temp folder.
     *
     * **Note:** This is an internal method and should not be called directly by external code.
     * It is automatically invoked by [clean].
     */
    @ApiStatus.Internal
    fun clearTempFonts() {
        val dir = File(System.getProperty("java.io.tmpdir"), "kui-fonts")
        if (dir.exists()) {
            dir.listFiles { file -> file.extension == "ttf" }?.forEach { it.delete() }
            dir.delete()
        }
    }

    /**
     * Represents a font resource entry loaded from JSON.
     *
     * **Note:** This is an internal class and should not be used by external code.
     *
     * @property file The filename of the font (e.g., "arial.ttf").
     * @property size The font size to use when loading.
     */
    @ApiStatus.Internal
    data class FontEntry(
        val file: String,
        val size: Int
    )

}