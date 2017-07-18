//package mobito.com.intextv.parser;
//
//import mobito.com.intextv.parser.contentcategoryjsonparser.JsonParserContentCategory;
//import mobito.com.intextv.parser.contenthomejsonparser.JsonParserContentHomeAPI;
//import mobito.com.intextv.parser.contentmoviejsonparser.JsonParserMovieContent;
//import mobito.com.intextv.parser.contentrecommendedjsonparser.JsonParserRecommendedContent;
//import mobito.com.intextv.parser.contentsonylivejsonparser.JsonParserSonyLiveContent;
//import mobito.com.intextv.parser.contentvideojsonparer.JsonParserVideoContent;
//import mobito.com.intextv.parser.dataclass.ContentCategoryData;
//import mobito.com.intextv.parser.dataclass.HomeContentData;
//import mobito.com.intextv.parser.dataclass.RecommendedContentData;
//import mobito.com.intextv.parser.dataclass.SonyLiveContentData;
//import mobito.com.intextv.parser.dataclass.MovieContentData;
//import mobito.com.intextv.parser.dataclass.VideoContentData;
//import AppConfig;
//import Tracer;
//
///**
// * Created by mkr on 10/28/2016.
// */
//
//public class JsonParser {
//    private static JsonParser BASE_PARSER;
//    private static final String TAG = AppConfig.BASE_TAG + ".JsonParser";
//
//    /**
//     * Private Constructor
//     */
//    private JsonParser() {
//        Tracer.debug(TAG, "JsonParser: ");
//    }
//
//    /**
//     * Method to get the Instance of this class
//     *
//     * @return
//     */
//    public static JsonParser getInstance() {
//        Tracer.debug(TAG, "getInstance: " + BASE_PARSER);
//        if (BASE_PARSER == null) {
//            BASE_PARSER = new JsonParser();
//        }
//        return BASE_PARSER;
//    }
//
//    /**
//     * Method to parse the Json received for home content. Caller should be called this method from the Back Thread
//     *
//     * @param jsonObjectString
//     * @return
//     */
//    public HomeContentData parseContentHomeJson(String jsonObjectString) {
//        Tracer.debug(TAG, "parseContentHomeJson: ");
//        return JsonParserContentHomeAPI.parseContentHomeJson(jsonObjectString);
//    }
//
//    /**
//     * Method to Parse the Json received at the Time of Fetching the Recommended content Json
//     *
//     * @param jsonObjectString
//     * @return
//     */
//    public static RecommendedContentData parseRecommendedContentJson(String jsonObjectString) {
//        Tracer.debug(TAG, "parseRecommendedContentJson: ");
//        return JsonParserRecommendedContent.parseRecommendedContentJson(jsonObjectString);
//    }
//
//    /**
//     * Method to Parse the Json received at the Time of Fetching the Movie content Json
//     *
//     * @param jsonObjectString
//     * @return
//     */
//    public static MovieContentData parseMovieContentJson(String jsonObjectString) {
//        Tracer.debug(TAG, "parseMovieContentJson: ");
//        return JsonParserMovieContent.parseMovieContentJson(jsonObjectString);
//    }
//
//    /**
//     * Method to Parse the Json received at the Time of Fetching the Video content Json
//     *
//     * @param jsonObjectString
//     * @return
//     */
//    public static VideoContentData parseVideoContentJson(String jsonObjectString) {
//        return JsonParserVideoContent.parseVideoContentJson(jsonObjectString);
//    }
//
//    /**
//     * Method to Parse the Json received at the Time of Fetching the SonyLive content Json
//     *
//     * @param jsonObjectString
//     * @return
//     */
//    public static SonyLiveContentData parseSonyLiveContentJson(String jsonObjectString) {
//        Tracer.debug(TAG, "parseSonyLiveContentJson: ");
//        return JsonParserSonyLiveContent.parseSonyLiveContentJson(jsonObjectString);
//    }
//
//    /**
//     * Method to parse the Json received for Content Category Api
//     *
//     * @param jsonObjectString
//     * @return
//     */
//    public static ContentCategoryData parseContentCategoryJson(String jsonObjectString) {
//        return JsonParserContentCategory.parseContentCategoryJson(jsonObjectString);
//    }
//
//
//}
