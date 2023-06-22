package com.example.dialogtree;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private List<String> selectedDirectories = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Приложение запросило доступ к каталогу!");
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        ListView listView = new ListView(this);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        File rootDirectory = Environment.getExternalStorageDirectory();
        File[] directories = rootDirectory.listFiles();
        List<String> directoryList = new ArrayList<>();
        if (directories != null) {
            for (File directory : directories) {
                if (directory.isDirectory()) {
                    directoryList.add(directory.getAbsolutePath());
                }
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_multiple_choice, directoryList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedDirectory = directoryList.get(position);
            if (selectedDirectories.contains(selectedDirectory)) {
                selectedDirectories.remove(selectedDirectory);
            } else {
                selectedDirectories.add(selectedDirectory);
            }
        });
        layout.addView(listView);
        builder.setView(layout);
        builder.setPositiveButton("OK", (dialog, which) -> {
            for (String directory : selectedDirectories) {
                System.out.println(directory);
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            selectedDirectories.clear();
            for (String directory : selectedDirectories) {
                System.out.println(directory);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
    /*
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Button buttonDialog = new Button()
        public void onClick(View view) {
            MyDialogFragment myDialogFragment = new MyDialogFragment();
            FragmentManager manager = getSupportFragmentManager();

            //myDialogFragment.show(manager, "myDialog");
            FragmentTransaction transaction = manager.beginTransaction();
            myDialogFragment.show(transaction, "dialog");

        }

    }

    public class MyDialogFragment extends DialogFragment {
        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Запрос доступа к каталогу")
                    .setMessage("Приложение запросило  доступ к каталогу. Определите каталоги, к которым будет разрешен доступ:")
                    .setPositiveButton("Сохранить", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            builder.setNegativeButton("Отменить", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(getActivity(), "Доступ к каталогам не был определен",
                            Toast.LENGTH_LONG).show();
                }
            });
            builder.setCancelable(true);
            return builder.create();
        }

        //public static String TAG = "PurchaseConfirmationDialog";
    }

    public class MyDialogFragment extends DialogFragment {

    }*/
