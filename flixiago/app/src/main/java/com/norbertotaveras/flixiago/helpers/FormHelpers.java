package com.norbertotaveras.flixiago.helpers;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.norbertotaveras.flixiago.R;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class FormHelpers {

    private static final String TAG = "FormHelpers";
    private static final String IMAGES_FILE_PROVIDER = "com.norbertotaveras.flixiago.fileprovider";

    public static String getViewText(TextInputEditText view) {
        Editable editable = view.getText();
        if (editable != null)
            return editable.toString();
        return "";
    }

    public static void showGenreMenu(
            final OnDynamicMenuItemSelected listener,
            final View menuButton,
            final HashMap<Long, String> genreMap,
            final long currentGenreId) {
        // Build an array of map entries as an array so we can sort them
        final ArrayList<Pair<Long, String>> entries =
                new ArrayList<>(genreMap.size() + 1);

        int i = 0;
        entries.add(new Pair<>(-1L, "All"));
        for (Map.Entry<Long, String> entry : genreMap.entrySet())
            entries.add(new Pair<>(entry.getKey(), entry.getValue()));

        Object[] entryArray = new Object[entries.size()];
        entries.toArray(entryArray);

        // Sort them by genre text
        Arrays.sort(entryArray, new Comparator<Object>() {
            @Override
            public int compare(Object p1, Object p2) {
                Pair<Long, String> o1 = (Pair<Long, String>) p1;
                Pair<Long, String> o2 = (Pair<Long, String>) p2;
                if (o1.first == -1)
                    return -1;
                if (o2.first == -1)
                    return 1;
                return o1.second.compareTo(o2.second);
            }
        });

        entries.clear();
        for (Object entry : entryArray)
            entries.add((Pair<Long, String>) entry);

        // Make an array of text for the menu
        final String[] genres = new String[genreMap.size() + 1];

        int highlightIndex = -1;
        for (i = 0; i < entries.size(); ++i) {
            genres[i] = entries.get(i).second;

            if (entries.get(i).first == currentGenreId)
                highlightIndex = i;
        }

        // Make the menu
        final FormHelpers.DynamicMenu genreMenu =
                new FormHelpers.DynamicMenu(menuButton, highlightIndex, genres);

        // Show the menu and handle clicks
        genreMenu.show(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // Figure out which entry they clicked
                int index = genreMenu.indexOf(item);

                long selectedGenreId = entries.get(index).first;

                listener.genreIdSelected(selectedGenreId);

                return true;
            }
        });
    }

    public static boolean openGoogleSearch(Context context, String query) {
        try {
            return FormHelpers.openUrl(context,
                    "https://www.google.com/search?q=" +
                            URLEncoder.encode(query, "utf8"));
        } catch (UnsupportedEncodingException ex) {
            return false;
        }
    }

    private static boolean openUrl(Context context, String url) {
        Uri uri = Uri.parse(url);
        return FormHelpers.openUri(context, uri);
    }

    private static boolean openUri(Context context, Uri uri) {
        Intent openBrowserIntent = new Intent(Intent.ACTION_VIEW);
        openBrowserIntent.setData(uri);
        context.startActivity(openBrowserIntent);
        return true;
    }

    public static void toggleFavoriteButton(ImageButton watchButton, boolean setting) {
        toggleImageButton(watchButton, setting,
                R.drawable.ic_baseline_favorite_24,
                R.drawable.ic_baseline_favorite_border_24);
    }

    public static void toggleFavoriteView(ImageView watchView, boolean setting) {
        toggleImageVIew(watchView, setting,
                R.drawable.ic_round_favorite_24,
                R.drawable.ic_round_favorite_border_24);
    }

    public static void toggleImageButton(
            ImageButton watchButton, boolean setting,
            @DrawableRes int onResource, @DrawableRes int offResource) {
        Resources resources = watchButton.getResources();

        Drawable image = ResourcesCompat.getDrawable(resources,
                setting ? onResource : offResource, null);

        watchButton.setImageDrawable(image);
    }

    public static void toggleImageVIew(
            ImageView watchView,
            boolean setting,
            @DrawableRes int onResource, @DrawableRes int offResource) {
        Resources resources = watchView.getResources();

        Drawable image = ResourcesCompat.getDrawable(resources,
                setting ? onResource : offResource, null);

        watchView.setImageDrawable(image);
    }

    public interface ConfirmResultListener {
        void onConfirmResult(boolean confirmed);
    }

    public static void confirm(final ConfirmResultListener listener,
                               Context context,
                               @StringRes int message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message);
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listener.onConfirmResult(true);
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listener.onConfirmResult(false);
            }
        });
        builder.show();
    }

    public interface MultipleChoiceListener {
        void choice(int which);
    }

    public static void multipleChoice(final MultipleChoiceListener listener,
                                      Context context,
                                      boolean addCancel,
                                      @StringRes int title,
                                      @StringRes final int... choices) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        Resources resources = context.getResources();
        CharSequence[] buttons = new CharSequence[choices.length];
        int i = 0;
        for (int choice : choices)
            buttons[i++] = resources.getString(choice);
        builder.setItems(buttons, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listener.choice(choices[which]);
            }
        });
        builder.show();
    }

    public interface ThumbnailClickListener<T> {
        void onClick(T item);
    }

    // generic method to populate scrolling image list
    public static <T extends InternetImage> void populateScrollingImageList(
            Fragment fragment, LinearLayout container,
            List<T> items, boolean circular, @LayoutRes int layoutId,
            boolean showCaption, final ThumbnailClickListener<T> listener) {
        container.removeAllViews();
        View view;

        final ArrayList<View> views = new ArrayList<>(items.size());

        for (final T item : items) {
            view = fragment.getLayoutInflater().inflate(
                    layoutId, container, false);
            ImageView imageView = view.findViewById(R.id.image);
            TextView captionView = view.findViewById(R.id.caption);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClick(item);
                }
            });

            String thumbnailUrl = item.getThumbnailUrl();
            String thumbnailCaption = item.getThumbnailCaption();

            if (!circular) {
                Glide.with(fragment)
                        .load(thumbnailUrl)
                        .apply(RequestOptions.placeholderOf(R.color.design_default_color_primary))
                        .into(imageView);
            } else {
                Glide.with(fragment)
                        .load(item.getThumbnailUrl())
                        .apply(RequestOptions.placeholderOf(R.color.design_default_color_primary))
                        .apply(RequestOptions.circleCropTransform())
                        .into(imageView);
            }

            if (captionView != null) {
                if (thumbnailCaption != null)
                    captionView.setText(thumbnailCaption);

                captionView.setVisibility(showCaption ? View.VISIBLE : View.GONE);
            }

            views.add(view);
            container.addView(view);
        }

        for (View itemView : views)
            itemView.forceLayout();

        container.requestLayout();
    }


    public static void showSnackbar(Fragment fragment, @StringRes int resid) {
        if (fragment == null) {
            Log.e(TAG, "No fragment!");
            return;
        }

        View view = fragment.getView();
        if (view == null) {
            Log.e(TAG, "No view!");
            return;
        }

        Snackbar.make(view, resid, Snackbar.LENGTH_LONG).show();
    }

    public static void showSnackbar(Fragment fragment, String text) {
        if (fragment == null) {
            Log.e(TAG, "No fragment!");
            return;
        }

        View view = fragment.getView();
        if (view == null) {
            Log.e(TAG, "No view!");
            return;
        }

        Snackbar.make(view, text, Snackbar.LENGTH_LONG).show();
    }

    public static void toggleSwitchButton(Button view, boolean on,
                                          @StringRes int onText, int offText) {
        view.setText(on ? onText : offText);
    }

    private static final SimpleDateFormat dateFormatter = new SimpleDateFormat(
            "MMM d, y", Locale.US);

    public static String replaceEmpty(String text, String emptyText) {
        if (text != null && !text.isEmpty())
            return text;
        return emptyText;
    }

    public static String formatDate(String dateText) {
        if (dateText == null)
            return "";

        String[] parts = dateText.split("-");
        int y, m, d;
        try {
            y = Integer.parseInt(parts[0]);
            m = Integer.parseInt(parts[1]);
            d = Integer.parseInt(parts[2]);
        } catch (NumberFormatException ex) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.set(y, m - 1, d, 0, 0, 0);
        Date date = calendar.getTime();
        return dateFormatter.format(date);
    }

    public static class DynamicMenu {
        PopupMenu popupMenu;
        List<MenuItem> items;

        public DynamicMenu(View view, int highlightIndex, String... options) {
            Context context = view.getContext();

            popupMenu = new PopupMenu(context, view);
            Menu menu = popupMenu.getMenu();

            items = new ArrayList<>(options.length);

            for (int i = 0; i < options.length; ++i) {
                MenuItem item = menu.add(options[i]);
                item.setCheckable(i == highlightIndex);
                item.setChecked(i == highlightIndex);
                items.add(item);
            }
        }

        public void show(PopupMenu.OnMenuItemClickListener clickListener) {
            popupMenu.setOnMenuItemClickListener(clickListener);

            popupMenu.show();
        }

        public int indexOf(MenuItem item) {
            return items.indexOf(item);
        }
    }

    public interface OnDynamicMenuItemSelected {
        void genreIdSelected(long genreId);
    }

    public static class AsyncCount {
        public int completed;
        public int pending;

        public AsyncCount(int pending) {
            this.pending = pending;
        }
    }

    public interface OnImagePicked {
        void imagePicked(Bitmap bitmap, Exception error);
    }

    public static class StoragePermissionHelper {
        Activity activity;
        int requestCode;
        PermissionCallback callback;

        public StoragePermissionHelper(Activity activity, int requestCode) {
            this.activity = activity;
            this.requestCode = requestCode;
        }

        public void withStoragePermission(PermissionCallback callback) {
            int permissionResult = ContextCompat.checkSelfPermission(
                    activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if (permissionResult == PackageManager.PERMISSION_GRANTED) {
                callback.onResult(true);
            } else {
                this.callback = callback;
                ActivityCompat.requestPermissions(
                        activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        requestCode);
            }
        }

        public boolean onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                                  @NonNull int[] grantResults) {
            if (requestCode != this.requestCode)
                return false;

            if (callback == null) {
                Log.e(TAG, "Got permission result but permissionCallback is null!");
                return false;
            }

            // scan through the permissions list
            for (int i = 0; i < permissions.length; ++i) {
                if (permissions[i].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    // it is the external storage permission, access the corresponding
                    // entry of grantResult to see if the user allowed it
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        // the user granted permission
                        assert callback != null;
                        callback.onResult(true);
                    } else {
                        // the user denied permission to access external storage
                        assert callback != null;
                        callback.onResult(false);
                    }
                    callback = null;
                    break;
                }
            }

            return true;
        }
    }

    public interface PermissionCallback {
        void onResult(boolean havePermission);
    }

    public static class ImagePicker {
        private final Activity activity;
        private final int libraryRequestCode;
        private final int cameraRequestCode;
        private final StoragePermissionHelper storagePermissionHelper;
        private final Fragment fragment;
        private OnImagePicked callback;

        public File photoFile;
        public Uri photoUri;

        public ImagePicker(Fragment fragment, int libraryRequestCode, int cameraRequestCode,
                           StoragePermissionHelper storagePermissionHelper) {
            this.fragment = fragment;
            this.activity = fragment.getActivity();
            this.libraryRequestCode = libraryRequestCode;
            this.cameraRequestCode = cameraRequestCode;
            this.storagePermissionHelper = storagePermissionHelper;
        }

        public void show(OnImagePicked callback) {
            this.callback = callback;

            FormHelpers.multipleChoice(new FormHelpers.MultipleChoiceListener() {
                                           @Override
                                           public void choice(int which) {
                                               switch (which) {
                                                   case R.string.library:
                                                       getImageFromLibrary();
                                                       break;

                                                   case R.string.camera:
                                                       storagePermissionHelper.withStoragePermission(new PermissionCallback() {
                                                           @Override
                                                           public void onResult(boolean havePermission) {
                                                               if (havePermission)
                                                                   getImageFromCamera();
                                                           }
                                                       });
                                                       break;
                                               }
                                           }
                                       }, activity, true, R.string.image_source,
                    R.string.library, R.string.camera);
        }

        public void onActivityResult(View view, int requestCode, int resultCode, Intent data) {
            if (requestCode == cameraRequestCode) {
                if (resultCode != Activity.RESULT_OK) {
                    Log.v(TAG, "activity result not ok");
                    callback.imagePicked(null, new Exception("Cancelled"));
                    return;
                }

                Uri selectedImage = photoUri;
                //context.getContentResolver().notifyChange(selectedImage, null);
                ContentResolver cr = activity.getContentResolver();
                Bitmap bitmap;
                try {
                    bitmap = android.provider.MediaStore.Images.Media
                            .getBitmap(cr, selectedImage);

                    callback.imagePicked(bitmap, null);
                } catch (Exception e) {
                    Snackbar.make(view, R.string.load_failed, Snackbar.LENGTH_LONG).show();
                    Log.e("Camera", e.toString());
                    callback.imagePicked(null, e);
                }
            } else if (requestCode == libraryRequestCode) {
                if (resultCode != Activity.RESULT_OK) {
                    Log.v(TAG, "activity result not ok");
                    callback.imagePicked(null, new Exception("Cancelled"));
                    return;
                }

                if (data == null) {
                    Log.w(TAG, "Got OK with null data from pick intent!");
                    callback.imagePicked(null, new Exception("Got null data"));
                    return;
                }

                Uri imageUri = data.getData();

                Bitmap choice;
                try {
                    choice = MediaStore.Images.Media.getBitmap(
                            activity.getContentResolver(), imageUri);
                } catch (IOException e) {
                    Log.e(TAG, "Failed to decode image from library pick data");
                    return;
                }

                callback.imagePicked(choice, null);
            }
        }

        private void getImageFromLibrary() {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/");
            fragment.startActivityForResult(intent, libraryRequestCode);
        }


        private void getImageFromCamera() {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            File externalFilesDir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES);

            photoFile = createImageFile(externalFilesDir);

            photoUri = FileProvider.getUriForFile(activity, IMAGES_FILE_PROVIDER, photoFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);

            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION |
                    Intent.FLAG_GRANT_READ_URI_PERMISSION);

            fragment.startActivityForResult(intent, cameraRequestCode);
        }


        private File createImageFile(File dir) {
            // Create an image file name
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                    .format(new Date());

            String imageFileName = "JPEG_" + timeStamp + ".jpg";

            File imageFile = new File(dir, imageFileName);

            try {
                if (!imageFile.createNewFile())
                    return null;
            } catch (IOException e) {
                return null;
            }

            return imageFile;
        }
    }
}