package ru.epserv.proxycheck.v3.api.model.response

import com.mojang.serialization.Codec
import org.jetbrains.annotations.ApiStatus

/**
 * Type of historical attack associated with the IP address.
 *
 * Attack types are not standardized. New ones may be introduced (and existing ones may be removed)
 * within the same API version, which is the reason why [AttackType] is not an enum class.
 *
 * @property id attack type identifier
 * @since 1.0.0
 * @author metabrix
 * @see [AttackType.fromId]
 */
@ConsistentCopyVisibility
@ApiStatus.AvailableSince("1.0.0")
data class AttackType private constructor(val id: String) {
    companion object {
        private val cache = mutableMapOf<String, AttackType>()

        fun fromId(id: String): AttackType {
            return synchronized(cache) { cache.getOrPut(id) { AttackType(id) } }
        }

        @ApiStatus.Internal
        val CODEC: Codec<AttackType> = Codec.STRING.xmap(::fromId, AttackType::id)

        val LOGIN_ATTEMPT = fromId("login_attempt")

        val REGISTRATION_ATTEMPT = fromId("registration_attempt")

        val COMMENT_SPAM = fromId("comment_spam")

        val DENIAL_OF_SERVICE = fromId("denial_of_service")

        val FORUM_SPAM = fromId("forum_spam")

        val FORM_SUBMISSION = fromId("form_submission")

        val PAYMENT_FRAUD = fromId("payment_fraud")

        val BOTNET_ZOMBIE = fromId("botnet_zombie")

        val PROBING = fromId("probing")

        val BRUTE_FORCE = fromId("brute_force")

        val SPOOFING = fromId("spoofing")

        val CVE_EXPLOIT = fromId("cve_exploit")
    }
}
