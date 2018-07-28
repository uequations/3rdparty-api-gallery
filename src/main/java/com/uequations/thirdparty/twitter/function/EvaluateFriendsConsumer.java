package com.uequations.thirdparty.twitter.function;

import com.uequations.thirdparty.twitter.config.TwitterConfigConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.PagableResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;

/**
 * @author Mensah Alkebu-Lan <malkebu-lan@uequations.com>
 */
public class EvaluateFriendsConsumer implements BiConsumer<Integer, Integer> {

    private final Twitter twitter;
    private static final String SCREEN_NAME = "uequations";
    private static final int NEW_USER_THRESHHOLD = 400;
    private static final int PAUSE_TIME_SECONDS = 24;
    private static final int FRIENDS_TO_FOLLOWERS_RATION = 125;

    private static final Logger LOGGER = LoggerFactory.getLogger(EvaluateFriendsConsumer.class);

    public EvaluateFriendsConsumer(Twitter twitter) {
        this.twitter = twitter;
    }

    @Override
    public void accept(Integer startPage, Integer endPage) {

        LOGGER.info("Will end processing after {} iterations.", endPage);

        AtomicInteger numDeleted = new AtomicInteger(0);
        AtomicInteger numPages = new AtomicInteger(0);

        int cursor;

        for (cursor = startPage; cursor <= endPage; cursor++) {

            PagableResponseList<User> twitterUsers = null;

            try {
                twitterUsers = twitter.getFriendsList(SCREEN_NAME, -1, TwitterConfigConstants.PAGECOUNT);

                if (twitterUsers.size() <= 0) {
                    return;
                }
            } catch (TwitterException e) {
                LOGGER.error(e.getErrorMessage(), e);

                if (e.exceededRateLimitation()) {
                    LOGGER.info("Seconds until reset: {}", e.getRateLimitStatus().getSecondsUntilReset());
                }
            }

            if (twitterUsers == null) {
                return;
            }

            twitterUsers.forEach(user -> {

                int numberOfFriendsFriends = user.getFriendsCount();
                int numberOfFriendsFollowers = user.getFollowersCount();
                String screenName = user.getScreenName();

                boolean tooManyFriends = numberOfFriendsFriends > (numberOfFriendsFollowers / FRIENDS_TO_FOLLOWERS_RATION);

                if (tooManyFriends) {
                    LOGGER.debug("{} has {} friends and {} followers.", screenName, numberOfFriendsFriends, numberOfFriendsFollowers);
                    if (numberOfFriendsFriends > NEW_USER_THRESHHOLD) {

                        try {
                            LOGGER.info("Unfriending {} with {} friends.", screenName, numberOfFriendsFriends);
                            twitter.destroyFriendship(screenName);
                            numDeleted.getAndIncrement();
                        } catch (TwitterException e) {
                            LOGGER.error(e.getMessage(), e);
                        }

                    }
                }
            });
            numPages.getAndIncrement();

            try {
                LOGGER.info("Pausing for {} seconds to avoid exceeeding rate limit.", PAUSE_TIME_SECONDS);
                Thread.sleep(PAUSE_TIME_SECONDS * 1000);
            } catch (InterruptedException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        LOGGER.info("Unfollowed {} friends.", numDeleted.get());
        LOGGER.info("Looked through {} pages ending on page {}.", numPages.get(), endPage);
    }
}

