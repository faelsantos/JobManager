package com.fsdeveloper.jobmanager.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.fsdeveloper.jobmanager.R;
import com.fsdeveloper.jobmanager.activity.JobFormActivity;
import com.fsdeveloper.jobmanager.activity.JobPreview;
import com.fsdeveloper.jobmanager.adapter.JobListAdapter;
import com.fsdeveloper.jobmanager.bean.Client;
import com.fsdeveloper.jobmanager.bean.Job;
import com.fsdeveloper.jobmanager.bean.JobCategory;
import com.fsdeveloper.jobmanager.bean.Phone;
import com.fsdeveloper.jobmanager.exception.ConnectionException;
import com.fsdeveloper.jobmanager.exception.JobManagerException;
import com.fsdeveloper.jobmanager.manager.Manager;
import com.fsdeveloper.jobmanager.tool.MyDataTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author Created by Douglas Rafael on 06/05/2016.
 * @version 1.0
 */
public class JobListFragment extends ListFragment implements ActionMode.Callback, AdapterView.OnItemLongClickListener {
    private final String TAG = "JobListFragment";
    private final String RESULT_JOB = "job";
    private final int REQUEST_JOB = 1;
    private Manager manager;
    private Context context;
    private ListView mListView;
    private ActionMode mActionMode;
    private JobListAdapter mAdapter;
    private Menu mMenu;
    private List<Job> listOfJobs;

    /**
     * The Constructor
     *
     * @param context
     */
    public JobListFragment(Context context) {
        this.context = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list_fragment, null, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mListView = getListView();
        // Setting Event
        getListView().setOnItemLongClickListener(this);

        // Get list the jobs
        listOfJobs = new ArrayList<Job>(getListJobs());

        // Setting Adapter
        mAdapter = new JobListAdapter(this, context, listOfJobs);
        setListAdapter(mAdapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        if (mActionMode == null) {
            Job job = (Job) l.getItemAtPosition(position);

            Activity activity = getActivity();
            if (activity instanceof OnJobClick) {
                OnJobClick listener = (OnJobClick) activity;
                listener.onJobClick(job);
            }

            /**
             * Open view detail job
             */
            if (job != null) {
                Log.i("onListItemClick", "OPEN Preview job " + job);
                Intent intent = new Intent(context, JobPreview.class);
                intent.putExtra(RESULT_JOB, job);
                startActivityForResult(intent, REQUEST_JOB);
            }
        } else {
            int checkedCount = updateItemsChange(mListView, position);
            if (checkedCount == 0) {
                mActionMode.finish();
            }
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        boolean consumed = (mActionMode == null);

        if (consumed) {
            Log.i(TAG, "onItemLongClick");
            AppCompatActivity activity = (AppCompatActivity) getActivity();

            mActionMode = activity.startSupportActionMode(this);
            mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

            mListView.setItemChecked(i, true);
            updateItemsChange(mListView, i);
        }

        return consumed;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        Log.i(TAG, "onCreateActionMode");
        getActivity().getMenuInflater().inflate(R.menu.list_item_menu, menu);

        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        this.mMenu = menu;
        changeActionBar(true);
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        SparseBooleanArray checked = mListView.getCheckedItemPositions();
        int position = 0;
        switch (item.getItemId()) {
            case R.id.action_list_edit:
                // Edit item
                position = checked.keyAt(0);
                Job job = (Job) mListView.getItemAtPosition(position);
                if (job != null) {
                    Intent intent = new Intent(context, JobFormActivity.class);
                    intent.putExtra(JobFormActivity.RESULT_JOB, job);
                    startActivityForResult(intent, JobFormActivity.REQUEST_JOB);
                }

                mActionMode.finish();
                return true;
            case R.id.action_list_delete:
                // Open dialog and and treats the return in onActivityResult
                GenericDialogFragment dialogRemove = GenericDialogFragment.newDialog(
                        1, R.string.action_confirm_delete, new int[]{android.R.string.ok, android.R.string.cancel}, JobListFragment.this);
                dialogRemove.setTargetFragment(this, GenericDialogFragment.REQUEST_DIALOG);
                dialogRemove.show(getFragmentManager());

                return true;
            case R.id.action_list_select_all:
                selectAllItems(mListView);
        }
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == getActivity().RESULT_OK && requestCode == JobFormActivity.REQUEST_JOB) {
            Log.i(TAG, "onActivityResult()");
            updateListView();
        } else if (requestCode == GenericDialogFragment.REQUEST_DIALOG && resultCode == Activity.RESULT_OK) {
            removeItemsChecked();
        }
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        Log.i(TAG, "onDestroyActionMode");
        mActionMode = null;
        mListView.clearChoices();
        mListView.setChoiceMode(ListView.CHOICE_MODE_NONE);
        changeActionBar(false);

        updateListView();
    }

    /**
     * Remove items checked in listView
     *
     * @return True if success or False otherwise.
     */
    private boolean removeItemsChecked() {
        SparseBooleanArray checked = mListView.getCheckedItemPositions();
        boolean result = false;
        int position = 0;
        for (int i = 0; i < checked.size(); i++) {
            if (checked.valueAt(i)) {
                position = checked.keyAt(i);

                Job j = (Job) mListView.getItemAtPosition(position);
                result = deleteJob(j);
            }
        }

        if (result) {
            String message = context.getResources().getQuantityString(R.plurals.success_delete_job, checked.size(), checked.size());
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

            mActionMode.finish();
        }

        return result;
    }

    /**
     * Update listView
     */
    private void updateListView() {
        listOfJobs.clear();
        listOfJobs.addAll(getListJobs());
        mAdapter.notifyDataSetChanged();
    }


    /**
     * Update items in  ListView
     *
     * @param l        The listView
     * @param position Position the item in listView
     * @return Total items selected
     */
    private int updateItemsChange(ListView l, int position) {
        SparseBooleanArray checked = l.getCheckedItemPositions();
        l.setItemChecked(position, l.isItemChecked(position));
        int checkedCount = 0;
        if (checked != null) {
            for (int i = 0; i < checked.size(); i++) {
                if (checked.valueAt(i)) {
                    checkedCount++;
                }
            }
            processActionMode(checkedCount);
        }

        return checkedCount;
    }

    /**
     * Processes ActionMode.
     * Defines which buttons should appear and the title.
     *
     * @param checkedCount The total items checked in ListView.
     */
    private void processActionMode(int checkedCount) {
        if (checkedCount == 1) {
            mMenu.findItem(R.id.action_list_edit).setVisible(true);
        } else {
            mMenu.findItem(R.id.action_list_edit).setVisible(false);
        }

        // Setting title in mActionMode
        String selected = context.getResources().getQuantityString(R.plurals.number_selected, checkedCount, checkedCount);
        mActionMode.setTitle(selected);
    }

    /**
     * Select all items in ListView
     *
     * @param v The ListView
     */
    private void selectAllItems(ListView v) {
        int checkedCount = v.getCount();
        for (int i = 0; i < checkedCount; i++) {
            v.setItemChecked(i, true);
        }

        processActionMode(checkedCount);
    }

    /**
     * Sets the Action Bar should be shown or not.
     *
     * @param hide True should be removed should be False if it should be shown.
     */
    public void changeActionBar(boolean hide) {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ActionBar actionBar = activity.getSupportActionBar();

        if (hide) {
            actionBar.hide();
        } else {
            actionBar.show();
        }
    }

    /**
     * Get list teh jobs
     *
     * @return List the job
     */
    private List<Job> getListJobs() {
        List<Job> result = new ArrayList<Job>();
        try {
            manager = new Manager(context);

            result = manager.listOfJobs(1);  // TODO Mudar pelo id do user
        } catch (ConnectionException e) {
            Toast.makeText(context, R.string.error_bd, Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } catch (JobManagerException e) {
            Toast.makeText(context, R.string.error_system, Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Interface to see if the item was clicked.
     */
    public interface OnJobClick {
        void onJobClick(Job job);
    }


    /**
     * Delete job selected.
     *
     * @param job The job
     * @return True if success or False otherwise.
     */
    private boolean deleteJob(Job job) {
        try {
            if (manager.deleteJob(job)) {
                return true;
            }
        } catch (JobManagerException e) {
            Toast.makeText(context, context.getResources().getString(R.string.error_delete_job), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        return false;
    }

    //TODO Removet - apenas apara teste
    private void addJobsTest(Manager manager) throws JobManagerException {
        List<JobCategory> categories = new ArrayList<JobCategory>();

        List<Phone> phones = new ArrayList<Phone>();

        for (int i = 50; i <= 150; i++) {
            categories.add(new JobCategory(new Random().nextInt(10) + 1, ""));
            Client client = new Client("Fulano de Tal " + i, "fulanodetal" + i + "@mail.com", "", null, 1, phones);
            manager.insertJob(new Job(manager.generateProtocol(), "Title of the Job " + i, "Lorem Ipsum é simplesmente uma simulação de texto da indústria tipográfica e de impressos, e vem sendo utilizado desde o século XVI,",
                    "Ao contrário do que se acredita, Lorem Ipsum não é simplesmente um texto randômico. Com mais de 2000 anos, suas raízes podem ser encontradas em uma obra de literatura latina clássica datada de 45 AC. ",
                    15.0 * i, 0.0, null, MyDataTime.getDataTime(context.getResources().getString(R.string.date_time_bd)), 1, client, categories));
//                public Job(String protocol, String title, String description, String note, Double price, Double expense, String finalized_at, String created_at, int user_id, Client client, List<JobCategory> categories) {

        }
    }
}
