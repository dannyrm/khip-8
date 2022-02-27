import com.github.dannyrm.khip8.Khip8Bootstrap
import com.github.dannyrm.khip8.config.Config
import com.github.dannyrm.khip8.config.FrontEndType.JAVA_AWT
import com.github.dannyrm.khip8.config.loadConfig
import com.github.dannyrm.khip8.display.view.SwingUi
import com.github.dannyrm.khip8.display.view.Ui
import com.github.dannyrm.khip8.input.KeyboardManager
import com.github.dannyrm.khip8.util.logger
import com.github.dannyrm.khip8.sound.SoundGenerator
import org.koin.core.module.Module
import org.koin.dsl.module

object JvmKhip8Bootstrap {
    private val LOG = logger(this::class)

    @JvmStatic
    fun main(args: Array<String>) {
        val config = loadConfig()

        if (args.isNotEmpty()) {
            Khip8Bootstrap.boot(args[0], config, listOf(loadDependencies(config)))
        } else {
            LOG.error { "ERROR: ROM file expected as parameter" }
        }
    }

    private fun loadDependencies(config: Config): Module = module {
        single { SoundGenerator(config.soundConfig) }

        if (config.frontEndConfig.frontEnd == JAVA_AWT) {
            single { KeyboardManager(get(), get()) }
            single<Ui> { SwingUi(get(), get(), config) }
        }
    }
}
