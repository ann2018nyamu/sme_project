package ke.co.eclectic.quickstore.helper;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;

import java.util.HashMap;

import ke.co.eclectic.quickstore.R;
import ke.co.eclectic.quickstore.activities.auth.AuthActivity;
import ke.co.eclectic.quickstore.models.Country;
import ke.co.eclectic.quickstore.models.User;
import ke.co.eclectic.quickstore.viewModel.UserViewModel;

/**
 * Created by David Manduku on 08/10/2018.
 */
public class GlobalVariable {


    public static Country choosenCountry;

    public static Fragment currentFragment;
    public static Integer storeid=-1;
    private static User currentUser = new User();

    /**
     * Sets current user.
     *
     * @param currentUser the current user
     */
    public static void setCurrentUser(User currentUser) {
        GlobalVariable.currentUser = currentUser;
    }


    /**
     * Gets current user.
     *
     * @return the current user
     */
    public static User getCurrentUser() {
        return currentUser;
    }

    /**
     * Get montserrat light typeface.
     *
     * @param context the context
     * @return the typeface
     */
    public  static Typeface getMontserratLight(Context context){
        // Create a TypeFace using font from Assets folder
        return ResourcesCompat.getFont(context, R.font.montserrat_light);
    }

    /**
     * Get montserrat regular typeface.
     *
     * @param context the context
     * @return the montserrat typeface
     */
    public  static Typeface getMontserratRegular(Context context){
        // Create a TypeFace using font from Assets folder

        return ResourcesCompat.getFont(context, R.font.montserrat_regular);
    }

    /**
     * Get montserrat medium typeface.
     *
     * @param context the context
     * @return the montserrat typeface
     */
    public static Typeface getMontserratMedium(Context context){
        // Create a TypeFace using font from Assets folder
        return ResourcesCompat.getFont(context, R.font.montserrat_medium);
    }

    /**
     * Get montserrat semi bold typeface.
     *
     * @param context the context
     * @return the montserrat typeface
     */
    public  static Typeface getMontserratSemiBold(Context context){
        // Create a TypeFace using font from Assets folder
        return ResourcesCompat.getFont(context, R.font.montserrat_semibold);
    }


}
