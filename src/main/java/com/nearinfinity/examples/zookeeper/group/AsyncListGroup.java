package com.nearinfinity.examples.zookeeper.group;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import com.nearinfinity.examples.zookeeper.util.ConnectionWatcher;
import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.KeeperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AsyncListGroup extends ConnectionWatcher {

    private static final Logger LOG = LoggerFactory.getLogger(AsyncListGroup.class);

    public static void main(String[] args) throws Exception {
        AsyncListGroup asyncListGroup = new AsyncListGroup();
        asyncListGroup.connect(args[0]);
        asyncListGroup.list(args[1]);
        asyncListGroup.close();
    }

    public void list(final String groupName) throws KeeperException, InterruptedException {
        String path = "/" + groupName;

        // In real code, you would not use the async API the way it's being used here. You would
        // go off and do other things without blocking like this example does.
        final CountDownLatch latch = new CountDownLatch(1);
        zk.getChildren(path, false,
                new AsyncCallback.ChildrenCallback() {
                    @Override
                    public void processResult(int rc, String path, Object ctx, List<String> children) {
                        LOG.info("Called back for path {} with return code {}", path, rc);
                        if (children == null) {
                            LOG.info("Group {} does not exist", groupName);
                        } else {
                            if (children.isEmpty()) {
                                LOG.info("No members in group {}", groupName);
                                return;
                            }
                            for (String child : children) {
                                LOG.info(child);
                            }
                        }
                        latch.countDown();
                    }
                }, null /* optional context object */);
        LOG.info("Awaiting latch countdown...");
        latch.await();
    }

}
