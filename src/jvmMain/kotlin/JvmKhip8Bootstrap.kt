import com.github.dannyrm.khip8.Khip8Bootstrap
import com.github.dannyrm.khip8.config.loadConfig
import com.github.dannyrm.khip8.sound.SoundGenerator
import org.koin.dsl.module

object JvmKhip8Bootstrap {

    @JvmStatic
    fun main(args: Array<String>) {
        val config = loadConfig()

        val modules = module {
            single { SoundGenerator(config.soundConfig) }
        }

        Khip8Bootstrap.boot(config, listOf(modules))
    }
}
