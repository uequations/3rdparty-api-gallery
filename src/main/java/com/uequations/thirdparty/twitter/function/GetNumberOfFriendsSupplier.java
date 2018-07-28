package com.uequations.thirdparty.twitter.function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import java.util.function.Supplier;


/**
 * @author Mensah Alkebu-Lan <malkebu-lan@uequations.com>
 */
public class GetNumberOfFriendsSupplier implements Supplier<Integer> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetNumberOfFriendsSupplier.class);

    private final Twitter twitter;

    public GetNumberOfFriendsSupplier(Twitter twitter) {
        this.twitter = twitter;
    }

    @Override
    public Integer get() {
        long[] friendsIDs = new long[0];
        try {
            friendsIDs = twitter.getFriendsIDs(-1).getIDs();
        } catch (TwitterException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return friendsIDs.length;
    }
}
