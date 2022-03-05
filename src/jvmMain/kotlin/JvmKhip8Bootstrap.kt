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

    @JvmStatic
    fun main(args: Array<String>) {
        val config = loadConfig()
        Khip8Bootstrap.boot(config, listOf(loadDependencies(config)))
    }

    private fun loadDependencies(config: Config): Module = module {
        single { SoundGenerator(config.soundConfig) }

        if (config.frontEndConfig.frontEnd == JAVA_AWT) {
            single { KeyboardManager(get(), get()) }
            single<Ui> { SwingUi(get(), get(), config) }
        }
    }
}
