package io.github.mthli.Tweetin.Fragment.TweetList;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import io.github.mthli.Tweetin.Flag.FlagUnit;
import io.github.mthli.Tweetin.Fragment.Base.ListFragment;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.Task.TweetList.InReplyToTask;
import io.github.mthli.Tweetin.Tweet.Tweet;
import io.github.mthli.Tweetin.Tweet.TweetAdapter;

import java.util.ArrayList;
import java.util.List;

public class InReplyToFragment extends ListFragment {
    private TweetAdapter tweetAdapter;
    public TweetAdapter getTweetAdapter() {
        return tweetAdapter;
    }

    private List<Tweet> tweetList = new ArrayList<Tweet>();
    public List<Tweet> getTweetList() {
        return tweetList;
    }

    private int previousPosition = 0;
    public void setPreviousPosition(int previousPosition) {
        this.previousPosition = previousPosition;
    }

    private InReplyToTask inReplyToTask;

    private int taskStatus = FlagUnit.TASK_IDLE;
    public void setTaskStatus(int taskStatus) {
        this.taskStatus = taskStatus;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initUI();

        inReplyToTask = new InReplyToTask(this, false);
        inReplyToTask.execute();
    }

    private void initUI() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!isSomeTasksRunning()) {
                    inReplyToTask = new InReplyToTask(InReplyToFragment.this, true);
                    inReplyToTask.execute();
                }
            }
        });

        tweetAdapter = new TweetAdapter(getActivity(), R.layout.tweet, tweetList);
        listView.setAdapter(tweetAdapter);
        tweetAdapter.notifyDataSetChanged();

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            private int currentFirst = 0;
            private int currentCount = 0;

            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                if (scrollState == SCROLL_STATE_IDLE && (previousPosition < currentFirst || previousPosition > currentFirst + currentCount)) {
                    tweetList.get(previousPosition).setDetail(false);
                    tweetAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                currentFirst = firstVisibleItem;
                currentCount = visibleItemCount;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int currentPosition, long id) {
                if (previousPosition == currentPosition) {
                    tweetList.get(currentPosition).setDetail(!tweetList.get(currentPosition).isDetail());
                } else {
                    tweetList.get(previousPosition).setDetail(false);
                    tweetList.get(currentPosition).setDetail(true);
                }
                tweetAdapter.notifyDataSetChanged();

                previousPosition = currentPosition;
            }
        });
    }

    public boolean isSomeTasksRunning() {
        return taskStatus == FlagUnit.TASK_RUNNING;
    }

    public void cancelAllTasks() {
        if (inReplyToTask != null && inReplyToTask.getStatus() == AsyncTask.Status.RUNNING) {
            inReplyToTask.cancel(true);
        }
    }
}
