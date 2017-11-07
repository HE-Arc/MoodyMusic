package ch.hearc.moodymusic.detection;

import com.github.mhendred.face4j.AsyncAdapter;
import com.github.mhendred.face4j.AsyncFaceClient;
import com.github.mhendred.face4j.FaceApi;
import com.github.mhendred.face4j.RequestListener;
import com.github.mhendred.face4j.examples.AsyncExample;
import com.github.mhendred.face4j.exception.FaceServerException;
import com.github.mhendred.face4j.model.Photo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static ch.hearc.moodymusic.tools.Constants.SKY_API_KEY;
import static ch.hearc.moodymusic.tools.Constants.SKY_API_SEC;

/**
 * Created by axel.rieben on 07.11.2017.
 */

public class DetectionEngine extends com.github.mhendred.face4j.examples.ClientExample {

    /**
     * A url of a photo with faces in it
     */
    protected static final String URL_WITH_FACES = "http://seedmagazine.com/images/uploads/attractive_article.jpg";

    private static final Logger logger = LoggerFactory.getLogger(AsyncExample.class);
    private AsyncFaceClient faceClient;

    RequestListener listener = new AsyncAdapter() {
        @Override
        public void onDetect(Photo photo) {
            logger.info("Success: ", photo);
        }

        @Override
        public void onFaceServerException(FaceServerException fse, FaceApi faceApi) {
            logger.error("Error: ", fse);
        }
    };

    public DetectionEngine() {
//        faceClient = new DefaultFaceClient(SKY_API_KEY, SKY_API_SEC);
        faceClient = new AsyncFaceClient(SKY_API_KEY, SKY_API_SEC);
        faceClient.addListener(listener);
    }


    public void detect() {
        faceClient.detect(URL_WITH_FACES);
//        try {
////            Photo photo = faceClient.detect(URL_WITH_FACES).get(0);
//        } catch (FaceClientException e) {
//            e.printStackTrace();
//        } catch (FaceServerException e) {
//            e.printStackTrace();
//        }
    }
}
