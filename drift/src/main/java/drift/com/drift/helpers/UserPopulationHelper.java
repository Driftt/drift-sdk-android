package drift.com.drift.helpers;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.content.res.AppCompatResources;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import drift.com.drift.R;
import drift.com.drift.model.User;



public class UserPopulationHelper {
    public static void populateTextAndImageFromUser(Context context, User user, @Nullable TextView textView, ImageView imageView) {

        Uri uriToLoad = null;
        Boolean ignoreUri = false;
        if (user != null) {

            if (textView != null) {
                textView.setText(user.getUserName());
            }

            if (user.bot) {
                ignoreUri = true;
                Drawable placeholderDrawable = AppCompatResources.getDrawable(context, R.drawable.robot);
                Drawable backgroundDrawable = AppCompatResources.getDrawable(context, R.drawable.drift_sdk_bot_background);

                DrawableCompat.setTint(backgroundDrawable, ColorHelper.getBackgroundColor());


                Drawable[] layers = new Drawable[2];
                layers[0] = backgroundDrawable;
                layers[1] = placeholderDrawable;
                LayerDrawable layerDrawable = new LayerDrawable(layers);
                imageView.setImageDrawable(layerDrawable);


            } else if (user.avatarUrl != null) {
                uriToLoad = Uri.parse(user.avatarUrl);
            }

        } else {
            //Unknown User
            if (textView != null) {
                textView.setText("Unknown User");
            }
        }

        if (!ignoreUri) {

            RequestOptions requestOptions = new RequestOptions()
                    .circleCrop()
                    .placeholder(R.drawable.placeholder);


            Glide.with(context)
                    .load(uriToLoad)
                    .apply(requestOptions)
                    .into(imageView);
        }
    }
}
