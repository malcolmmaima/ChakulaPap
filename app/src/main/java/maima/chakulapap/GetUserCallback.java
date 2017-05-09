package maima.chakulapap;

/**
 * Created by malcolm on 3/10/2017.
 */

interface GetUserCallback {
    /**
     * Invoked when background task is completed
     */

    public abstract void done(User returnedUser);
}

