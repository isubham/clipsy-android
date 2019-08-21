package com.subhamkumar.clipsy.panel;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.jkcarino.rtexteditorview.RTextEditorButton;
import com.jkcarino.rtexteditorview.RTextEditorToolbar;
import com.jkcarino.rtexteditorview.RTextEditorView;
import com.subhamkumar.clipsy.R;
import com.subhamkumar.clipsy.models.Clip;
import com.subhamkumar.clipsy.models.ClipApiResonse;
import com.subhamkumar.clipsy.panel.fragments.InsertLinkDialogFragment;
import com.subhamkumar.clipsy.models.Constants;
import com.subhamkumar.clipsy.utils.Tools;
import com.subhamkumar.clipsy.utils.wrapper;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.subhamkumar.clipsy.utils.Tools.getTimeStamp;

public class editor extends AppCompatActivity {

    public static String initialEditorHtml = "Here it goes ...";


    private Clip currentclip;

    private void showNetworkUnavailableDialog() {
        final Dialog dialog = new Dialog(editor.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_network_unavailable_confirmation);

        dialog.findViewById(R.id.dialog_nonet_exit).setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.findViewById(R.id.dialog_nonet_continue).setOnClickListener(v -> {
            dialog.dismiss();
            applyChangesToClip(lastAction);
        });

        dialog.show();

    }


    private int clipUpdateRequestCode = 123;


    private void closeEditor() {
        setResult(RESULT_OK);
        this.finish();
    }

    private String getToken() {
        return Objects.requireNonNull(getIntent().getExtras()).getString("token");
    }

    private String getClipId() {
        return Objects.requireNonNull(getIntent().getExtras()).getString("clip_id");
    }

    private String lastAction;

    private void showSaveVisibilityDialog() {

        final Dialog showSaveVisibility = new Dialog(this);

        showSaveVisibility.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(showSaveVisibility.getWindow()).setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.FILL_PARENT);

        showSaveVisibility.setContentView(R.layout.dialog_save_clip_confirmation);

        showSaveVisibility.findViewById(R.id.dialog_save_clip_public).setOnClickListener(v -> {
            lastAction = Constants.visibility_public;
            applyChangesToClip(Constants.visibility_public);
            showSaveVisibility.dismiss();
        });

        showSaveVisibility.findViewById(R.id.dialog_save_clip_private).setOnClickListener(v -> {
            lastAction = Constants.visibility_private;
            applyChangesToClip(Constants.visibility_private);
            showSaveVisibility.dismiss();
        });

        showSaveVisibility.findViewById(R.id.dialog_save_clip_cancel).setOnClickListener(v -> {
            showSaveVisibility.dismiss();
        });

        showSaveVisibility.show();

    }


    private void applyChangesToClip(String visibility) {
        if (ClipChanged()) {

            Tools.showNetworkLoadingDialog(networkLoadingDialog, "editor show");

            if (currentclip.clip_id.equals(Constants.response_invalid_clip_id)) {
                saveClip(visibility);
            } else {
                updateClip(currentclip.clip_id, visibility);
            }

        } else {
            Tools.showSnackBar(parent, "Nothing to Save", Color.RED);
        }
    }

    RelativeLayout parent;

    private enum EditorStates {
        TitleEmptyContentUntouched,
        TitleEmptyContentTouched,
        TitleEmptyContentWritten,
        TitleWrittenContentUntouched,
        TitleWrittenContentTouched,
        TitleWrittenContentWritten,

        UpdateTitleUntouchedContentUntouched,
    }

    private String EditorStateMessage(EditorStates editorStates) {
        HashMap<EditorStates, String> message = new HashMap<>();
        message.put(EditorStates.TitleEmptyContentUntouched, "Title and content are empty.");
        message.put(EditorStates.TitleEmptyContentTouched, "Title and content are empty.");
        message.put(EditorStates.TitleEmptyContentWritten, "Title is empty.");
        message.put(EditorStates.TitleWrittenContentUntouched, "Content is empty.");
        message.put(EditorStates.TitleWrittenContentTouched, "Content is empty.");
        message.put(EditorStates.UpdateTitleUntouchedContentUntouched, "No updates to save");

        return message.get(editorStates);
    }

    private EditorStates getEditorState() {

        if (getTitleString().isEmpty() && getContentString().isEmpty()) {
            return EditorStates.TitleEmptyContentTouched;
        }

        if(getTitleString().isEmpty() && getContentString().equals(initialEditorHtml)) {
            return EditorStates.TitleEmptyContentUntouched;
        }

        if ((getContentString().equals(initialEditorHtml) && !getTitleString().isEmpty())) {
            return EditorStates.TitleWrittenContentUntouched;
        }

        if (getTitleString().isEmpty() && !getContentString().equals(initialEditorHtml) && !getContentString().isEmpty()) {
            return EditorStates.TitleEmptyContentWritten;
        }
        if (!getTitleString().isEmpty() && getContentString().isEmpty()) {
            return EditorStates.TitleWrittenContentTouched;
        }
        if (!getTitleString().isEmpty() && !getContentString().isEmpty() && !getContentString().equals(initialEditorHtml)) {
            return EditorStates.TitleWrittenContentWritten;
        }
        if(getContentString().equals(fetchedClip) && getTitleString().equals(fetchedTitle)) {
            return EditorStates.UpdateTitleUntouchedContentUntouched;
        }
        else{
            return EditorStates.UpdateTitleUntouchedContentUntouched;
        }
    }



    private boolean ClipSavable() {
        EditorStates editorStates = getEditorState();
        if (updateOperation()) {
            if(editorStates != EditorStates.UpdateTitleUntouchedContentUntouched) {
                return true;
            }
            else {
                Tools.showSnackBar(parent, EditorStateMessage(editorStates), Color.RED);
                return false;
            }
        }
        else {
            if (editorStates == EditorStates.TitleWrittenContentWritten)
            {
                return true;
            }
            else {
                Tools.showSnackBar(parent, EditorStateMessage(editorStates), Color.RED);
                return false;
            }
        }
    }


    private boolean ClipChanged() {
        EditorStates editorStates = getEditorState();
        if (updateOperation()) {
            if(editorStates != EditorStates.UpdateTitleUntouchedContentUntouched) {
                return true;
            }
            else {
                Tools.showSnackBar(parent, EditorStateMessage(editorStates), Color.RED);
                return false;
            }
        }
        else {
            if (
                    editorStates == EditorStates.TitleEmptyContentUntouched
                 || editorStates == EditorStates.TitleEmptyContentTouched)
            {
                Tools.showSnackBar(parent, EditorStateMessage(editorStates), Color.RED);
                return false;
            }
            else {
                return true;
            }
        }
    }



    private void unSavedDialogCheck() {
        if (ClipChanged()) {

            final Dialog dialog = new Dialog(editor.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_unsaved_clip_confirmation);

            dialog.findViewById(R.id.dialog_unsaved_clip_menu_save).setOnClickListener(v -> {
                dialog.dismiss();
                showSaveVisibilityDialog();
            });
            dialog.findViewById(R.id.dialog_unsaved_clip_menu_close).setOnClickListener(v -> {
                dialog.dismiss();
                finish();
            });

            dialog.show();

        } else {
            finish();
        }
    }

    private boolean isActivityVisible() {
        return editor.this.isActivityVisible();
    }

    private static final String TAG = "RTextEditorView";

    private static final int DIALOG_TEXT_FORE_COLOR_ID = 0;
    private static final int DIALOG_TEXT_BACK_COLOR_ID = 1;

    private RTextEditorView editor;

    private void setHtml(String html) {
        editor.setHtml(html);
    }

    private Dialog networkLoadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        focusCounter = 0;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editor);

        Objects.requireNonNull(getSupportActionBar()).setElevation(0);
        networkLoadingDialog = new Dialog(editor.this, R.style.TranslucentDialogTheme);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        init();


        // Insert Link
        RTextEditorButton insertLinkButton = findViewById(R.id.insert_link);
        insertLinkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InsertLinkDialogFragment dialog = InsertLinkDialogFragment.newInstance();
                dialog.setOnInsertClickListener(onInsertLinkClickListener);
                dialog.show(getSupportFragmentManager(), "insert-link-dialog");
            }
        });
    }

    @Override
    public void onBackPressed() {
        unSavedDialogCheck();
    }


    private Bundle bundle;

    private int focusCounter;

    private boolean firstTimeFocused() {
        return focusCounter == 1;
    }

    private void init() {

        parent = findViewById(R.id.editorView);
        editor = findViewById(R.id.editor_view);
        // Enable keyboard's incognito mode
        editor.setIncognitoModeEnabled(true);

        RTextEditorToolbar editorToolbar = findViewById(R.id.editor_toolbar);
        editorToolbar.setEditorView(editor);

        editor.setOnTextChangeListener(content -> {
            // TODO add autosave feature
        });

        setExistingClipOrDefaultClip();

    }

    String fetchedClip, fetchedTitle;

    private void setExistingClipOrDefaultClip() {
        currentclip = new Clip();
        currentclip.clip_id = Constants.response_invalid_clip_id;

        bundle = getIntent().getExtras();

        if (updateOperation()) {
            fetchClip();
        } else {
            setHtml(initialEditorHtml);
            editor.setOnFocusChangeListener((v, hasFocus) -> {
                focusCounter++;
                if (firstTimeFocused()) {
                    setHtml("");
                }
            });
        }
    }

    private boolean updateOperation() {
        return Objects.requireNonNull(bundle).containsKey("clip");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {

            case R.id.action_undo:
                editor.undo();
                break;
            case R.id.action_redo:
                editor.redo();
                break;
            case R.id.create_menu_write:
                if(ClipSavable()) showSaveVisibilityDialog();
                break;
            case android.R.id.home:
                unSavedDialogCheck();
            default:
                return true;
        }
        return true;

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        editor.setOnTextChangeListener(null);
        editor.removeAllViews();
        editor.destroy();
        editor = null;
    }

    private final InsertLinkDialogFragment.OnInsertClickListener onInsertLinkClickListener =
            new InsertLinkDialogFragment.OnInsertClickListener() {
                @Override
                public void onInsertClick(@NonNull String title, @NonNull String url) {
                    editor.insertLink(title, url);
                }
            };


    /*
     * network requests
     */
    private void fetchClip() {
        wrapper getClip = new wrapper() {
            @Override
            public Map makeParams() {
                return new HashMap<String, String>();
            }

            @Override
            protected void handleErrorResponse(VolleyError error) {
                showNetworkUnavailableDialog();
            }

            @Override
            public void handleResponse(String response) {
                ClipApiResonse apiResonse = new Gson().fromJson(response, ClipApiResonse.class);
                currentclip = apiResonse.data.get(0);

                fetchedTitle = currentclip.clip_title;
                fetchedClip = currentclip.clip_content;

                setEditorTitle(fetchedTitle);
                setEditorContent(fetchedClip);
            }

            @Override
            public void makeVolleyRequest(StringRequest stringRequest) {
                Volley.newRequestQueue(editor.this).add(stringRequest);
            }

            @Override
            public int setHttpMethod() {
                return Request.Method.GET;
            }

            @Override
            public String setHttpUrl() {
                String url = String.format(Constants.request_clip_read, getClipId());
                return url;
            }

            @Override
            public Map<String, String> _getHeaders() {
                Map params = new HashMap<String, String>();
                params.put(Constants.header_authentication, getToken());
                return params;
            }
        };

        getClip.makeRequest();
    }

    private void updateClip(final String clipId, String visibility) {
        wrapper update_clip = new wrapper() {
            @Override
            protected void handleErrorResponse(VolleyError error) {

                showNetworkUnavailableDialog();
            }

            @Override
            public Map<String, String> _getHeaders() {
                Map params = new HashMap<String, String>();
                params.put(Constants.header_authentication, getToken());
                return params;
            }

            @Override
            public Map makeParams() {
                Map params = new HashMap<String, String>();
                params.put(Constants.CLIP_CONTENT, editor.getHtml());
                params.put(Constants.param_visibility, visibility);

                params.put(Constants.CLIP_TIMESTAMP, getTimeStamp());
                params.put(Constants.CLIP_TITLE, getTitleString());

                Log.e("param", params.toString());
                return params;
            }

            @Override
            public void handleResponse(String response) {

                Gson gson = new Gson();
                ClipApiResonse clipApiResonse = gson.fromJson(response, ClipApiResonse.class);

                if (clipApiResonse.success.equals("1")) {
                    currentclip = clipApiResonse.data.get(0);
                    Tools.showSnackBar(parent, clipApiResonse.message, Color.GREEN);
                    closeEditor();
                } else {
                    // TODO handle no changes or empty or error
                    Tools.showSnackBar(parent, clipApiResonse.message, Color.RED);
                }

                Tools.hideNetworkLoadingDialog(networkLoadingDialog, "editor hide");
            }

            @Override
            public int setHttpMethod() {
                return Request.Method.POST;
            }

            @Override
            public String setHttpUrl() {
                return String.format(Constants.request_clip_update, clipId);
            }

            @Override
            public void makeVolleyRequest(StringRequest stringRequest) {
                Volley.newRequestQueue(editor.this).add(stringRequest);
            }
        };

        update_clip.makeRequest();
    }

    private void saveClip(String visibility) {

        wrapper saveClip = new wrapper() {

            @Override
            public Map<String, String> _getHeaders() {
                Map params = new HashMap<String, String>();
                params.put(Constants.header_authentication, getToken());
                return params;
            }

            @Override
            protected void handleErrorResponse(VolleyError error) {
                showNetworkUnavailableDialog();
            }

            // save clip
            @Override
            public Map makeParams() {
                Map params = new HashMap<String, String>();
                params.put(Constants.CLIP_CONTENT, editor.getHtml());
                params.put(Constants.CLIP_TIMESTAMP, getTimeStamp());
                params.put(Constants.CLIP_TITLE, getTitleString());

                params.put(Constants.param_visibility, visibility);

                return params;
            }

            @Override
            public int setHttpMethod() {
                return Request.Method.POST;
            }

            @Override
            public String setHttpUrl() {
                return Constants.request_clip_create;
            }

            @Override
            public void makeVolleyRequest(StringRequest stringRequest) {
                Volley.newRequestQueue(editor.this).add(stringRequest);
            }

            @Override
            public void handleResponse(String response) {
                Gson gson = new Gson();
                ClipApiResonse clipApiResonse = gson.fromJson(response, ClipApiResonse.class);

                Tools.showSnackBar(parent, clipApiResonse.message, Color.GREEN);
                if (clipApiResonse.success.equals("1")) {
                    currentclip = clipApiResonse.data.get(0);
                    closeEditor();
                } else {
                }

                Tools.hideNetworkLoadingDialog(networkLoadingDialog, "editor hide");

            }
        };
        saveClip.makeRequest();
    }

    // end of network


    // editor get and set
    private String getTitleString() {
        return ((EditText) findViewById(R.id.editor_title)).getText().toString().trim();
    }

    private void setEditorTitle(String title) {
        ((EditText) findViewById(R.id.editor_title)).setText(title);
    }


    private String getContentString() {
        return editor.getHtml();
    }

    private void setEditorContent(String content) {
        editor.setHtml(content);
    }
    // editor get and set



}
