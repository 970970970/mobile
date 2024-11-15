data class Brand(
    val id: Int,
    val name: String,
    val description: String?,
    val status: String?,
    @SerializedName("logo_media_id") val logoMediaId: Int?,
    @SerializedName("logo_path") val logoPath: String?,
    val reasons: List<String>?,
    val countries: List<String>?,
    val categories: List<String>?,
    val alternatives: List<String>?,
    val stakeholders: List<Stakeholder>?
)

data class Stakeholder(
    val id: String,
    val type: String
)

data class BrandListResponse(
    val total: Int,
    val items: List<Brand>
) 