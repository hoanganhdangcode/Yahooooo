package com.hoanganhdangcode.yahooooo.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hoanganhdangcode.yahooooo.Adapter.PostAdapter;
import com.hoanganhdangcode.yahooooo.Adapter.PostMediaAdapter;
import com.hoanganhdangcode.yahooooo.Model.MediaPost;
import com.hoanganhdangcode.yahooooo.Model.UserPost;
import com.hoanganhdangcode.yahooooo.R;
import com.hoanganhdangcode.yahooooo.Service.UploadMediaPostService;
import com.hoanganhdangcode.yahooooo.Util.Utils;
import com.hoanganhdangcode.yahooooo.ViewModel.FeedViewModel;

import java.util.ArrayList;
import java.util.List;

public class FeedFragment extends Fragment {

    private RecyclerView rvFeed, tempMediaRv;
    private EditText epostxt;
    private PostAdapter adapter;
    private PostMediaAdapter mediaAdapter;
    private FeedViewModel viewModel;
    private final List<Uri> selectedUris = new ArrayList<>();
    private ActivityResultLauncher<Intent> mediaPickerLauncher;
    private String currentUid;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_feed, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvFeed = view.findViewById(R.id.rvfeed);
        tempMediaRv = view.findViewById(R.id.tempmedia);
        epostxt = view.findViewById(R.id.epostxt);
        ImageButton mediaBtn = view.findViewById(R.id.mediabtn);
        Button postBtn = view.findViewById(R.id.btnpost);
        currentUid = Utils.getpref(requireContext(),"logined","uid");

        viewModel = new ViewModelProvider(this).get(FeedViewModel.class);

        adapter = new PostAdapter(requireContext(), new ArrayList<>());
        mediaAdapter = new PostMediaAdapter(requireContext(), new ArrayList<>());

        setupMediaPicker();
        setupMediaRecycler();
        setupPostListRecycler();

        mediaBtn.setOnClickListener(v -> pickMedia());

        postBtn.setOnClickListener(v -> {
            String text = epostxt.getText().toString().trim();
            if (text.isEmpty() && selectedUris.isEmpty()) {
                Toast.makeText(requireContext(), "Vui lòng nhập nội dung hoặc chọn media", Toast.LENGTH_SHORT).show();
                return;
            }

            String postId = Utils.genuuid();
            List<MediaPost> mediaPostList = new ArrayList<>();
            for (Uri uri : selectedUris) {
                mediaPostList.add(new MediaPost( "",uri.toString(), 0));
            }

            UserPost newPost = new UserPost(postId, currentUid, 0,text, mediaPostList,0, Utils.gettime(), 0,0);
            viewModel.createPost(newPost,requireContext());
            UploadMediaPostService.start(requireContext(), postId, "temp_uid");

            epostxt.setText("");
            selectedUris.clear();
            mediaAdapter.updateList(new ArrayList<>());
        });
    }

    private void pickMedia() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"image/*", "video/*"});
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        mediaPickerLauncher.launch(intent);
    }

    private void setupMediaRecycler() {
        tempMediaRv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        tempMediaRv.setAdapter(mediaAdapter);
    }

    private void setupPostListRecycler() {
        rvFeed.setLayoutManager(new LinearLayoutManager(getContext()));
        rvFeed.setAdapter(adapter);
        viewModel.getPostList().observe(getViewLifecycleOwner(), adapter::submitList);
    }

    private void setupMediaPicker() {
        mediaPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                        selectedUris.clear();
                        if (result.getData().getClipData() != null) {
                            for (int i = 0; i < result.getData().getClipData().getItemCount(); i++) {
                                selectedUris.add(result.getData().getClipData().getItemAt(i).getUri());
                            }
                        } else if (result.getData().getData() != null) {
                            selectedUris.add(result.getData().getData());
                        }

                        List<MediaPost> updated = new ArrayList<>();
                        for (Uri uri : selectedUris) {
                            updated.add(new MediaPost( "",uri.toString(), 0));
                        }
                        mediaAdapter.updateList(updated);
                    }
                });
    }
}