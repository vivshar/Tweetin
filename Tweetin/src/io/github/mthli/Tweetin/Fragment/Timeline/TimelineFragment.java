package io.github.mthli.Tweetin.Fragment.Timeline;

import android.app.AlertDialog;
import android.content.*;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.*;
import com.devspark.progressfragment.ProgressFragment;
import com.melnykov.fab.FloatingActionButton;
import io.github.mthli.Tweetin.Activity.Post.PostActivity;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.Task.Timeline.*;
import io.github.mthli.Tweetin.Unit.Anim.ActivityAnim;
import io.github.mthli.Tweetin.Unit.ContextMenu.ContextMenuAdapter;
import io.github.mthli.Tweetin.Unit.Flag.Flag;
import io.github.mthli.Tweetin.Unit.Tweet.Tweet;
import io.github.mthli.Tweetin.Unit.Tweet.TweetAdapter;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

import java.util.ArrayList;
import java.util.List;

public class TimelineFragment extends ProgressFragment {
    private View view;

    private int refreshFlag = Flag.TIMELINE_TASK_IDLE;
    private boolean moveToBottom = false;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FloatingActionButton floatingActionButton;
    public int getRefreshFlag() {
        return refreshFlag;
    }
    public void setRefreshFlag(int refreshFlag) {
        this.refreshFlag = refreshFlag;
    }
    public SwipeRefreshLayout getSwipeRefreshLayout() {
        return swipeRefreshLayout;
    }

    private TweetAdapter tweetAdapter;
    private List<Tweet> tweetList = new ArrayList<Tweet>();
    public TweetAdapter getTweetAdapter() {
        return tweetAdapter;
    }
    public List<Tweet> getTweetList() {
        return tweetList;
    }

    private SharedPreferences sharedPreferences;
    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    private Twitter twitter;
    private long useId;
    public Twitter getTwitter() {
        return twitter;
    }
    public long getUseId() {
        return useId;
    }

    private TimelineInitTask timelineInitTask;
    private TimelineMoreTask timelineMoreTask;
    private TimelineDeleteTask timelineDeleteTask;
    private TimelineRetweetTask timelineRetweetTask;
    private TimelineFavoriteTask timelineFavoriteTask;
    public boolean isSomeTaskRunning() {
        if (
                (timelineInitTask != null && timelineInitTask.getStatus() == AsyncTask.Status.RUNNING)
                || (timelineMoreTask != null && timelineMoreTask.getStatus() == AsyncTask.Status.RUNNING)
        ) {
            return true;
        }
        return false;
    }
    public void cancelAllTask() {
        if (timelineInitTask != null && timelineInitTask.getStatus() == AsyncTask.Status.RUNNING) {
            timelineInitTask.cancel(true);
        }
        if (timelineMoreTask != null && timelineMoreTask.getStatus() == AsyncTask.Status.RUNNING) {
            timelineMoreTask.cancel(true);
        }
        /* Do something */
        if (timelineRetweetTask != null && timelineRetweetTask.getStatus() == AsyncTask.Status.RUNNING) {
            timelineRetweetTask.cancel(true);
        }
        if (timelineFavoriteTask != null && timelineFavoriteTask.getStatus() == AsyncTask.Status.RUNNING) {
            timelineFavoriteTask.cancel(true);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setContentView(R.layout.timeline_fragment);
        view = getContentView();
        setContentEmpty(false);
        setContentShown(true);

        sharedPreferences = getActivity().getSharedPreferences(
                getString(R.string.sp_name),
                Context.MODE_PRIVATE
        );
        useId = sharedPreferences.getLong(
                getString(R.string.sp_use_id),
                -1
        );
        String consumerKey = sharedPreferences.getString(
                getString(R.string.sp_consumer_key),
                null
        );
        String consumerSecret = sharedPreferences.getString(
                getString(R.string.sp_consumer_secret),
                null
        );
        String accessToken = sharedPreferences.getString(
                getString(R.string.sp_access_token),
                null
        );
        String accessTokenSecret = sharedPreferences.getString(
                getString(R.string.sp_access_token_secret),
                null
        );
        TwitterFactory factory = new TwitterFactory();
        twitter = factory.getInstance();
        twitter.setOAuthConsumer(consumerKey, consumerSecret);
        AccessToken token = new AccessToken(accessToken, accessTokenSecret);
        twitter.setOAuthAccessToken(token);

        ListView listView = (ListView) view
                .findViewById(R.id.timeline_fragment_listview);
        tweetAdapter = new TweetAdapter(
                view.getContext(),
                R.layout.tweet,
                tweetList
        );
        listView.setAdapter(tweetAdapter);
        tweetAdapter.notifyDataSetChanged();

        swipeRefreshLayout = (SwipeRefreshLayout) view
                .findViewById(R.id.timeline_swipe_container);
        swipeRefreshLayout.setColorSchemeResources(
                R.color.text,
                R.color.secondary_text,
                R.color.text,
                R.color.secondary_text
        );
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                timelineInitTask = new TimelineInitTask(
                        TimelineFragment.this,
                        true
                );
                timelineInitTask.execute();
            }
        });

        floatingActionButton = (FloatingActionButton) view
                .findViewById(R.id.timeline_floating_action_button);
        floatingActionButton.attachToListView(listView);
        floatingActionButton.show();
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PostActivity.class);
                intent.putExtra(
                        getString(R.string.post_flag),
                        Flag.POST_ORIGINAL
                );
                ActivityAnim anim = new ActivityAnim();
                startActivity(intent);
                anim.fade(getActivity());
            }
        });
        floatingActionButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                /* Do something */
                return true;
            }
        });

        /* Do something */
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /* Do something */
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showItemLongClickDialog(position);
                return true;
            }
        });
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            private int previous = 0;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == SCROLL_STATE_IDLE) {
                    /* Do nothing */
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (previous < firstVisibleItem) {
                    moveToBottom = true;
                    floatingActionButton.hide();
                }
                if (previous > firstVisibleItem) {
                    moveToBottom = false;
                    floatingActionButton.show();
                }
                previous = firstVisibleItem;

                if (totalItemCount == firstVisibleItem + visibleItemCount) {
                    if (!isSomeTaskRunning() && moveToBottom) {
                        timelineMoreTask = new TimelineMoreTask(TimelineFragment.this);
                        timelineMoreTask.execute();
                    }
                }
            }
        });

        timelineInitTask = new TimelineInitTask(
                TimelineFragment.this,
                false
        );
        timelineInitTask.execute();
    }

    private AlertDialog alertDialog;
    private void reply(int loaction) {
        Intent intent = new Intent(getActivity(), PostActivity.class);
        ActivityAnim anim = new ActivityAnim();
        intent.putExtra(
                getString(R.string.post_flag),
                Flag.POST_REPLY
        );
        intent.putExtra(
                getString(R.string.post_status_id),
                tweetList.get(loaction).getStatusId()
        );
        intent.putExtra(
                getString(R.string.post_status_screen_name),
                tweetList.get(loaction).getScreenName()
        );
        startActivity(intent);
        anim.fade(getActivity());
    }
    private void quote(int location) {
        Intent intent = new Intent(getActivity(), PostActivity.class);
        ActivityAnim anim = new ActivityAnim();
        intent.putExtra(
                getString(R.string.post_flag),
                Flag.POST_QUOTE
        );
        intent.putExtra(
                getString(R.string.post_status_id),
                tweetList.get(location).getStatusId()
        );
        intent.putExtra(
                getString(R.string.post_status_screen_name),
                tweetList.get(location).getScreenName()
        );
        intent.putExtra(
                getString(R.string.post_status_text),
                tweetList.get(location).getText()
        );
        startActivity(intent);
        anim.fade(getActivity());
    }
    private void clip(int location) {
        ClipboardManager manager = (ClipboardManager) getActivity()
                .getSystemService(Context.CLIPBOARD_SERVICE);
        String text = tweetList.get(location).getText();
        ClipData data = ClipData.newPlainText(
                getString(R.string.tweet_copy_label),
                text
        );
        manager.setPrimaryClip(data);
        Toast.makeText(
                view.getContext(),
                R.string.tweet_notification_copy_successful,
                Toast.LENGTH_SHORT
        ).show();
    }
    private void multipleAtTwo(int flag, int location) {
        switch (flag) {
            case Flag.STATUS_NONE:
                timelineRetweetTask = new TimelineRetweetTask(
                        TimelineFragment.this,
                        location
                );
                timelineRetweetTask.execute();
                break;
            case Flag.STATUS_RETWEETED_BY_ME:
                Toast.makeText(
                        getActivity(),
                        R.string.context_toast_already_retweet,
                        Toast.LENGTH_SHORT
                ).show();
                break;
            case Flag.STATUS_SENT_BY_ME:
                /* Do something */
                break;
            default:
                break;
        }
    }
    private void showItemLongClickDialog(final int location) {
        LinearLayout linearLayout = (LinearLayout) getActivity()
                .getLayoutInflater().inflate(
                        R.layout.context_menu,
                        null
                );
        ListView menu = (ListView) linearLayout.findViewById(R.id.context_menu_listview);
        List<String> menuItemList = new ArrayList<String>();

        final int flag;
        final Tweet tweet = tweetList.get(location);
        menuItemList.add(getString(R.string.context_menu_item_reply));
        menuItemList.add(getString(R.string.context_menu_item_quote));
        if (tweet.getRetweetedByUserId() != -1 && tweet.getRetweetedByUserId() == useId) {
            flag = Flag.STATUS_RETWEETED_BY_ME;
            menuItemList.add(getString(R.string.context_menu_item_retweet));
        } else {
            if (tweet.getUserId() != useId) {
                flag = Flag.STATUS_NONE;
                menuItemList.add(getString(R.string.context_menu_item_retweet));
            } else {
                flag = Flag.STATUS_SENT_BY_ME;
                menuItemList.add(getString(R.string.context_menu_item_delete));
            }
        }
        menuItemList.add(getString(R.string.context_menu_item_favorite));
        menuItemList.add(getString(R.string.context_menu_item_copy));

        ContextMenuAdapter contextMenuAdapter = new ContextMenuAdapter(
                view.getContext(),
                R.layout.context_menu_item,
                menuItemList
        );
        menu.setAdapter(contextMenuAdapter);
        contextMenuAdapter.notifyDataSetChanged();

        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setView(linearLayout);
        builder.setCancelable(true);
        alertDialog = builder.create();
        alertDialog.show();

        menu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                switch (position) {
                    case 0:
                        reply(location);
                        alertDialog.hide();
                        alertDialog.dismiss();
                        break;
                    case 1:
                        quote(location);
                        alertDialog.hide();
                        alertDialog.dismiss();
                        break;
                    case 2:
                        multipleAtTwo(flag, location);
                        alertDialog.hide();
                        alertDialog.dismiss();
                        break;
                    case 3:
                        if (!tweet.isFavorite()) {
                            timelineFavoriteTask = new TimelineFavoriteTask(
                                    TimelineFragment.this,
                                    location
                            );
                            timelineFavoriteTask.execute();
                        } else {
                            Toast.makeText(
                                    getActivity(),
                                    R.string.context_toast_already_favorite,
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                        alertDialog.hide();
                        alertDialog.dismiss();
                        break;
                    case 4:
                        clip(location);
                        alertDialog.hide();
                        alertDialog.dismiss();
                        break;
                    default:
                        alertDialog.hide();
                        alertDialog.dismiss();
                        break;
                }
            }
        });
    }
}