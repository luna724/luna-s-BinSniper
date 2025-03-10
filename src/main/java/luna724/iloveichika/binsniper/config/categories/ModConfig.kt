package luna724.iloveichika.lunaclient.config.categories;

import com.google.gson.annotations.Expose;
import io.github.notenoughupdates.moulconfig.Config
import io.github.notenoughupdates.moulconfig.annotations.Category
import luna724.iloveichika.binsniper.BinSniper

class ModConfig : Config() {
    override fun getTitle(): String {
        return "§dLuna's BinSniper ${BinSniper.VERSION} by §dluna724§r, config menu by §channibal2§r, §5Moulberry §rand §5nea89";
    }

    override fun saveNow() {
        BinSniper.configManager.save()
    }
}