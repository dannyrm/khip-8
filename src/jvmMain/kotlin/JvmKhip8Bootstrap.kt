import com.github.dannyrm.khip8.Khip8Bootstrap
import com.github.dannyrm.khip8.config.FrontEndType
import com.github.dannyrm.khip8.config.FrontEndType.JAVA_AWT
import com.github.dannyrm.khip8.config.FrontEndType.KORGE
import com.github.dannyrm.khip8.config.loadConfig
import com.github.dannyrm.khip8.display.view.KorgeUi
import com.github.dannyrm.khip8.display.view.SwingUi
import com.github.dannyrm.khip8.display.view.Ui
import com.github.dannyrm.khip8.input.KeyboardManager
import com.github.dannyrm.khip8.util.logger
import com.github.dannyrm.khip8.sound.SoundGenerator
import org.koin.core.module.Module
import org.koin.dsl.module
import java.awt.Canvas

object JvmKhip8Bootstrap {
    private val LOG = logger(this::class)

    @JvmStatic
    fun main(args: Array<String>) {
        if (args.isNotEmpty()) {
            boot(args[0])
        } else {
            LOG.error { "ERROR: ROM file expected as parameter" }
        }
    }

    private fun boot(filePath: String, additionalModules: List<Module> = listOf()) {
        val modules = loadDependencies().plus(additionalModules)

        Khip8Bootstrap.boot(filePath, modules)
    }

    private fun loadDependencies(): Module {
        val config = loadConfig()

        return module {
            single { SoundGenerator(config.soundConfig) }

            when (config.frontEndConfig.frontEnd) {
                JAVA_AWT -> {
                    single { Canvas() }
                    single { KeyboardManager(get(), get()) }
                    single<Ui> { SwingUi(get(), get()) }
                }
                KORGE -> {
                    single<Ui> { KorgeUi() }
                }
            }
        }
    }
}