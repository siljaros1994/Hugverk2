package `is`.hbv601.hugverk2.model
//import android.os.Parcelable
//import kotlinx.parcelize.Parcelize
import java.io.Serializable

//@Parcelize
data class MyAppUser (
    val id: Long,
    val username: String, //Ensure this exits for favorite
    val email: String?,
    var isFavorited: Boolean = false //Add this property
) :Serializable //:Parcelable