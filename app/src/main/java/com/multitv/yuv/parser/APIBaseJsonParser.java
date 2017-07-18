//package mobito.com.intextv.parser;
//
//import java.util.ArrayList;
//
//import mobito.com.intextv.database.CategoryId;
//import mobito.com.intextv.database.DataBaseController;
//import mobito.com.intextv.database.MetaData;
//import mobito.com.intextv.database.PayPerView;
//import mobito.com.intextv.database.Program;
//import mobito.com.intextv.database.Thumbnail;
//import mobito.com.intextv.parser.dataclass.ProgramContentData;
//import mobito.com.intextv.parser.jsondataclass.JsonMetaData;
//import mobito.com.intextv.parser.jsondataclass.JsonPayPerView;
//import mobito.com.intextv.parser.jsondataclass.JsonProgram;
//import mobito.com.intextv.parser.jsondataclass.JsonThumbnail;
//import AppConfig;
//import Tracer;
//
///**
// * Base class for all the Json Parser who parse the json response received from API
// */
//public class APIBaseJsonParser {
//private static final String TAG = AppConfig.BASE_TAG + ".APIBaseJsonParser";
//    /**
//     * Method to parse the JsonProgramData list into ProgramContentDataList
//     *
//     * @param jsonProgramArrayList List of the Program
//     * @param dataBaseInputType    Denote the Input type of the Video Like : FEATURE_BANNER, HOME_CATEGORY, LIVE, RECOMENDED.<br> Saw <b>DataBaseInputType.Java</b>
//     * @return
//     */
//    protected static ArrayList<ProgramContentData> jsonProgramDataToProgramContentData(ArrayList<JsonProgram> jsonProgramArrayList, int dataBaseInputType) {
//        ArrayList<ProgramContentData> programContentDataArrayList = new ArrayList<>();
//        for (JsonProgram jsonProgram : jsonProgramArrayList) {
//            programContentDataArrayList.add(jsonProgramDataToProgramContentData(jsonProgram, dataBaseInputType));
//        }
//        return programContentDataArrayList;
//    }
//
//    /**
//     * Method to parse the JsonProgramData into ProgramContentData
//     *
//     * @param jsonProgram       Program data received from the server
//     * @param dataBaseInputType Denote the Input type of the Video Like : FEATURE_BANNER, HOME_CATEGORY, LIVE, RECOMENDED.<br> Saw <b>DataBaseInputType.Java</b>
//     * @return
//     */
//    protected static ProgramContentData jsonProgramDataToProgramContentData(JsonProgram jsonProgram, int dataBaseInputType) {
//        Program program = jsonProgramDataToProgramData(jsonProgram, dataBaseInputType);
//        MetaData metaData = jsonMetaDataToMetaData(jsonProgram.getMetaData());
//        ArrayList<CategoryId> categoryIdList = jsonCategoryIdToCategoryId(jsonProgram.getCategoryIdList(), jsonProgram.getProgramId());
//        ArrayList<Thumbnail> thumbnailList = jsonThumbnailToThumbnail(jsonProgram.getThumbnail(), jsonProgram.getProgramId());
//        PayPerView payPerView = jsonPayPerViewToPayPerView(jsonProgram.getPayPerViewInfo());
//        return new ProgramContentData(program, metaData, categoryIdList, thumbnailList, payPerView);
//    }
//
//    /**
//     * Method to parse the JsonProgramData into Program
//     *
//     * @param jsonProgram       Program data received from the server
//     * @param dataBaseInputType Denote the Input type of the Video Like : FEATURE_BANNER, HOME_CATEGORY, LIVE, RECOMENDED.<br> Saw <b>DataBaseInputType.Java</b>
//     * @return
//     */
//    protected static Program jsonProgramDataToProgramData(JsonProgram jsonProgram, int dataBaseInputType) {
//        Tracer.debug(TAG, "jsonProgramDataToProgramData: " + jsonProgram.getProgramId());
//        Long mId = jsonProgram.getProgramId();
//        String mTitle = jsonProgram.getTitle();
//        Integer mInputType = dataBaseInputType;
//        String mIndex = null;
//        String mDescription = jsonProgram.getDescription();
//        Integer mLanguageId = jsonProgram.getLanguageId();
//        String mLanguage = jsonProgram.getLanguage();
//        String mGenre = jsonProgram.getGenre();
//        String mMediaType = jsonProgram.getMediaType();
//        String mSource = jsonProgram.getSource();
//        Long mDuration = jsonProgram.getDuration();
//        String mPriceType = jsonProgram.getPriceType();
//        String mUrl = jsonProgram.getURL();
//        Integer mLikes = jsonProgram.getLiks();
//        Integer mLikeCount = jsonProgram.getLikeCount();
//        String mRatting = "" + jsonProgram.getRating();
//        Integer mWatch = jsonProgram.getWatch();
//        Integer mFavorite = jsonProgram.getFavorite();
//        String mKeyWords = jsonProgram.getKeyWords();
//        Long mUpdateTime = System.currentTimeMillis();
//        Integer mSocialLike = jsonProgram.getSocialLike();
//        Integer mSocialView = jsonProgram.getSocialView();
//        String mPackageInfo = jsonProgram.getPackageInfo();
//        return new Program(mId, mTitle, mInputType, mIndex, mDescription, mLanguageId, mLanguage, mGenre, mMediaType, mSource, mDuration, mPriceType, mUrl, mLikes, mLikeCount, mRatting, mWatch, mFavorite, mKeyWords, mUpdateTime, mSocialLike, mSocialView, mPackageInfo);
//    }
//
//    /**
//     * Method to parse the JsonPayPerView into PayPerView
//     *
//     * @param jsonPayPerView metaData correspond to the Json Program data received from the server
//     * @return
//     */
//    protected static PayPerView jsonPayPerViewToPayPerView(JsonPayPerView jsonPayPerView) {
//        Tracer.debug(TAG, "jsonMetaDataToMetaData: " + jsonPayPerView);
//        if (jsonPayPerView == null) {
//            return null;
//        }
//        Long mId = jsonPayPerView.getId();
//        Long mProgramId = null;
//        String mName = jsonPayPerView.getName();
//        String mCurrency = jsonPayPerView.getCurrency();
//        Float mPrice = jsonPayPerView.getPrice();
//        return new PayPerView(mId, mProgramId, mName, mCurrency, mPrice);
//    }
//
//    /**
//     * Method to parse the JsonMetaData into MetaData
//     *
//     * @param metaData metaData correspond to the Json Program data received from the server
//     * @return
//     */
//    protected static MetaData jsonMetaDataToMetaData(JsonMetaData metaData) {
//        Tracer.debug(TAG, "jsonMetaDataToMetaData: " + metaData);
//        Long mId = null;
//        Long mProgramId = null;
//        String mStarCast = metaData.getStarCast();
//        String mDirector = metaData.getDirector();
//        String mMusicDirector = metaData.getMusicDirector();
//        String mProducer = metaData.getProducer();
//        String mYear = metaData.getYear();
//        String mRated = metaData.getRated();
//        String mReleased = metaData.getReleased();
//        String mRunTime = metaData.getRuntime();
//        String mGenre = metaData.getGenre();
//        String mWriter = metaData.getWriter();
//        String mPlot = metaData.getPlot();
//        String mLanguage = metaData.getLanguage();
//        String mCountry = metaData.getCountry();
//        String mAward = metaData.getAward();
//        String mPoster = metaData.getPoster();
//        String mMetaSource = metaData.getMetascore();
//        String mImdbRating = metaData.getImdbRating();
//        String mImdbVote = metaData.getImdbVote();
//        String mImdbId = metaData.getImbdId();
//        String mType = metaData.getType();
//        return new MetaData(mId, mProgramId, mStarCast, mDirector, mMusicDirector, mProducer, mYear, mRated, mReleased, mRunTime, mGenre, mWriter, mPlot, mLanguage, mCountry, mAward, mPoster, mMetaSource, mImdbRating, mImdbVote, mImdbId, mType);
//    }
//
//    /**
//     * Method to parse the CategoryIdList of JsonProgramData into CategoryIdList
//     *
//     * @param jsonCategoryList List of category Ids are the JsonProgramData
//     * @param programId        Unique Id of the Program
//     * @return
//     */
//    protected static ArrayList<CategoryId> jsonCategoryIdToCategoryId(ArrayList<Long> jsonCategoryList, Long programId) {
//        Tracer.debug(TAG, "jsonCategoryIdToCategoryId: " + programId);
//        ArrayList<CategoryId> categoryIdArrayList = new ArrayList<>();
//        for (Long categoryId : jsonCategoryList) {
//            categoryIdArrayList.add(new CategoryId(null, programId, categoryId));
//        }
//        return categoryIdArrayList;
//    }
//
//
//    /**
//     * Method to create the JsonThumbnail data into Thumbnail data
//     *
//     * @param thumbnail Thumbnail data received from the JsonProgram data
//     * @return
//     */
//    protected static ArrayList<Thumbnail> jsonThumbnailToThumbnail(JsonThumbnail thumbnail, Long programId) {
//        Tracer.debug(TAG, "jsonThumbnailToThumbnail: " + programId);
//        ArrayList<Thumbnail> thumbnailArrayList = new ArrayList<>();
//        thumbnailArrayList.add(new Thumbnail(null, programId, DataBaseController.DataBaseConstants.DataBaseThumbnailType.LARGE, thumbnail.getLarge()));
//        thumbnailArrayList.add(new Thumbnail(null, programId, DataBaseController.DataBaseConstants.DataBaseThumbnailType.MEDIUM, thumbnail.getMedium()));
//        thumbnailArrayList.add(new Thumbnail(null, programId, DataBaseController.DataBaseConstants.DataBaseThumbnailType.SMALL, thumbnail.getSmall()));
//        return thumbnailArrayList;
//    }
//}
