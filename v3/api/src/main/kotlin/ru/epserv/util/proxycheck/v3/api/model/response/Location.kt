package ru.epserv.util.proxycheck.v3.api.model.response

import com.mojang.serialization.Codec
import ru.epserv.util.proxycheck.v3.api.util.mapCodec

data class Location(
    val continentName: String,
    val continentCode: String,
    val countryName: String,
    val countryCode: String,
    val regionName: String,
    val regionCode: String,
    val cityName: String,
    val postalCode: String,
    val latitude: Double,
    val longitude: Double,
    val timeZone: String,
    val currency: Currency,
) {
    companion object {
        val CODEC = mapCodec { instance ->
            instance.group(
                Codec.STRING.fieldOf("continent_name").forGetter(Location::continentName),
                Codec.STRING.fieldOf("continent_code").forGetter(Location::continentCode),
                Codec.STRING.fieldOf("country_name").forGetter(Location::countryName),
                Codec.STRING.fieldOf("country_code").forGetter(Location::countryCode),
                Codec.STRING.fieldOf("region_name").forGetter(Location::regionName),
                Codec.STRING.fieldOf("region_code").forGetter(Location::regionCode),
                Codec.STRING.fieldOf("city_name").forGetter(Location::cityName),
                Codec.STRING.fieldOf("postal_code").forGetter(Location::postalCode),
                Codec.DOUBLE.fieldOf("latitude").forGetter(Location::latitude),
                Codec.DOUBLE.fieldOf("longitude").forGetter(Location::longitude),
                Codec.STRING.fieldOf("timezone").forGetter(Location::timeZone),
                Currency.CODEC.fieldOf("currency").forGetter(Location::currency),
            ).apply(instance, ::Location)
        }
    }
}
