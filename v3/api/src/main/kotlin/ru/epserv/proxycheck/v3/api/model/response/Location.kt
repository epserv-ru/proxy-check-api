package ru.epserv.proxycheck.v3.api.model.response

import com.mojang.serialization.Codec
import org.jetbrains.annotations.ApiStatus
import ru.epserv.proxycheck.v3.api.util.mapCodec

/**
 * Location information.
 *
 * @property continentName name of the continent
 * @property continentCode code of the continent
 * @property countryName name of the country
 * @property countryCode ISO code of the country
 * @property regionName name of the region
 * @property regionCode code of the region
 * @property cityName name of the city
 * @property postalCode postal code
 * @property latitude latitude
 * @property longitude longitude
 * @property timeZone time zone
 * @property currency currency of the region
 * @since 1.0.0
 * @author metabrix
 */
@ApiStatus.AvailableSince("1.0.0")
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
        @ApiStatus.Internal
        internal val CODEC = mapCodec { instance ->
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
