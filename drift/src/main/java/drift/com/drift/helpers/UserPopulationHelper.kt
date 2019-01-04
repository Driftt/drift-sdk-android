package drift.com.drift.helpers

import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.net.Uri
import androidx.core.graphics.drawable.DrawableCompat
import androidx.appcompat.content.res.AppCompatResources
import android.widget.ImageView
import android.widget.TextView

import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

import drift.com.drift.R
import drift.com.drift.model.User


object UserPopulationHelper {
    fun populateTextAndImageFromUser(context: Context, user: User?, textView: TextView?, imageView: ImageView) {

        var uriToLoad: Uri? = null
        var ignoreUri: Boolean? = false
        if (user != null) {

            if (textView != null) {
                textView.text = user.userName
            }

            if (user.bot!!) {
                ignoreUri = true
                val placeholderDrawable = AppCompatResources.getDrawable(context, R.drawable.drift_sdk_robot)
                val backgroundDrawable = AppCompatResources.getDrawable(context, R.drawable.drift_sdk_bot_background)

                DrawableCompat.setTint(backgroundDrawable!!, ColorHelper.backgroundColor)


                val layers = arrayOfNulls<Drawable>(2)
                layers[0] = backgroundDrawable
                layers[1] = placeholderDrawable
                val layerDrawable = LayerDrawable(layers)
                imageView.setImageDrawable(layerDrawable)


            } else if (user.avatarUrl != null) {
                uriToLoad = Uri.parse(user.avatarUrl)
            }

        } else {
            //Unknown User
            textView?.setText(R.string.drift_sdk_unknown_user)
        }

        if ((!ignoreUri)!!) {

            val requestOptions = RequestOptions()
                    .circleCrop()
                    .placeholder(R.drawable.drift_sdk_placeholder)


            Glide.with(context)
                    .load(uriToLoad)
                    .apply(requestOptions)
                    .into(imageView)
        }
    }
}
