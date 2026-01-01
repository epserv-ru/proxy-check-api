package ru.epserv.proxycheck.v3.api.model.response

import com.mojang.serialization.Codec
import org.jetbrains.annotations.ApiStatus
import ru.epserv.proxycheck.v3.api.util.buildMapCodec
import ru.epserv.proxycheck.v3.api.util.codec.Codecs.forNullableGetter
import java.util.*
import kotlin.jvm.optionals.getOrNull

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
    val regionName: String?,
    val regionCode: String?,
    val cityName: String?,
    val postalCode: String?,
    val latitude: Double,
    val longitude: Double,
    val timeZone: String?,
    val currency: Currency,
) {
    constructor(
        continentName: String,
        continentCode: String,
        countryName: String,
        countryCode: String,
        regionName: Optional<String>,
        regionCode: Optional<String>,
        cityName: Optional<String>,
        postalCode: Optional<String>,
        latitude: Double,
        longitude: Double,
        timeZone: Optional<String>,
        currency: Currency,
    ) : this(
        continentName = continentName,
        continentCode = continentCode,
        countryName = countryName,
        countryCode = countryCode,
        regionName = regionName.getOrNull(),
        regionCode = regionCode.getOrNull(),
        cityName = cityName.getOrNull(),
        postalCode = postalCode.getOrNull(),
        latitude = latitude,
        longitude = longitude,
        timeZone = timeZone.getOrNull(),
        currency = currency,
    )

    companion object {
        @ApiStatus.Internal
        val CODEC = buildMapCodec { instance ->
            instance.group(
                Codec.STRING.fieldOf("continent_name").forGetter(Location::continentName),
                Codec.STRING.fieldOf("continent_code").forGetter(Location::continentCode),
                Codec.STRING.fieldOf("country_name").forGetter(Location::countryName),
                Codec.STRING.fieldOf("country_code").forGetter(Location::countryCode),
                Codec.STRING.optionalFieldOf("region_name").forNullableGetter(Location::regionName),
                Codec.STRING.optionalFieldOf("region_code").forNullableGetter(Location::regionCode),
                Codec.STRING.optionalFieldOf("city_name").forNullableGetter(Location::cityName),
                Codec.STRING.optionalFieldOf("postal_code").forNullableGetter(Location::postalCode),
                Codec.DOUBLE.fieldOf("latitude").forGetter(Location::latitude),
                Codec.DOUBLE.fieldOf("longitude").forGetter(Location::longitude),
                Codec.STRING.optionalFieldOf("timezone").forNullableGetter(Location::timeZone),
                Currency.CODEC.fieldOf("currency").forGetter(Location::currency),
            ).apply(instance, ::Location)
        }
    }
}
