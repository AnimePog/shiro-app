package com.lagradost.shiro.utils.extractors

import com.lagradost.shiro.utils.*

class MixDrop : ExtractorApi() {
    override val name: String = "MixDrop"
    override val mainUrl: String = "https://mixdrop.co"
    private val srcRegex = Regex("""wurl.*?=.*?"(.*?)";""")
    override val requiresReferer = true

    override fun getExtractorUrl(id: String): String {
        return "$mainUrl/e/$id"
    }

    override fun getUrl(url: String, referer: String?): List<ExtractorLink>? {
        try {
            with(khttp.get(url)) {
                getAndUnpack(this.text)?.let { unpackedText ->
                    srcRegex.find(unpackedText)?.groupValues?.get(1)?.let { link ->
                        return listOf(
                            ExtractorLink(
                                name,
                                httpsify(link),
                                url,
                                Qualities.Unknown.value,
                            )
                        )
                    }
                }
            }
            return null
        } catch (e: Exception) {
            return null
        }
    }
}