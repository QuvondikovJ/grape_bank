package com.example.uzum.helper;


import lombok.Data;

import java.util.Locale;

public class Messages {


    /*-------------======================== CATEGORY ========================-------------*/
    public static final String CATEGORY_ALREADY_EXIST = "This category already is added to parent category.";
    public static final String SUCH_CATEGORY_DOES_NOT_EXIST = "Such parent category does not exist.";
    public static final String CATEGORY_SAVED = "New category successfully saved.";

    public static final String NO_ANY_CATEGORY = "Any category is not added yet.";

    public static final String NO_ANY_NESTED_CATEGORY = "This category does not have any nested categories.";

    public static final String NO_ID_CATEGORY = "Such ID of Category does not exist.";

    public static final String CATEGORY_EDITED = "Category successfully updated.";

    public static final String CATEGORY_DELETED = "Category successfully deleted.";

    public static final String CATEGORY_RESTORED = "Category successfully restored.";

    public static final String NO_DELETED_CATEGORY = "Deleted categories do not exist.";
    public static final String SUCH_CATEGORY_ALREADY_EXIST = "Such category name already exists in this parent category, " +
            "if you want to recover this category, you must edit name of other category which already exists.";

    public static final String SUCH_CATEGORY_NOT_EXIST = "Such category does not exist in basket.";


    public static final String THIS_CATEGORY_IS_NOT_ADDED_ANY_PRODUCTS_YET = "This category is not added any products yet.";



    /*-------------======================== BRAND ========================-------------*/

    public static final String BRAND_ALREADY_EXIST = "This brand is already added.";
    public static final String BRAND_SAVED = "New brand successfully saved.";
    public static final String BRAND_NOT_ADDED_YET = "Brands are not added yet.";
    public static final String SUCH_BRAND_NOT_EXIST = "Such ID of Brand does not exist.";
    public static final String BRAND_UPDATED = "Brand name successfully updated.";
    public static final String BRAND_DELETED = "Brand successfully deleted.";
    public static final String THIS_BRAND_IS_NOT_ADDED_ANY_PRODUCTS_YET = "This brand is not added any products yet.";




    /*-------------======================== ATTACHMENT ========================-------------*/

    public static final Object EXCEED_DATA_FROM_LIMIT = "Cannot upload more than 10 file in one attempt";
    public static final String FILES_SAVED = "Files successfully saved.";
    public static final String ANY_FILES_NOT_SAVED = "Any files are not saved.";

    public static final String ENTER_ATTACH_IDS = "Please, enter IDs of attachment which need been changed.";
    public static final String UNAVAILABLE_DATA_TYPES = "All files are unavailable or their types are except needed extensions, please upload " +
            "only file extensions which are .jpe, .jpeg, .jpg, .jfif, .svg, .png";
    public static final String ANY_FILES_NOT_ADDED = "Any files are not added yet.";
    public static final String UNAVAILABLE_INPUT_DATA = "Unavailable input data, please enter accurate ID list.";
    public static final String ANY_FILES_NOT_ATTACHED = "This product has not been attached any files.";


    /*-------------======================== SELLER ========================-------------*/

    public static final String LOGO_IMAGE_IS_REQUIRED = "Logo image is required.";
    public static final String THIS_SELLER_ALREADY_ADDED = "This seller already is added.";
    public static final String INFO_MUST_BE_10_CHAR_AT_LEAST = "Info must be 10 character at least.";
    public static final String INFO_MUST_BE_1000_CHAR_AT_MOST = "Info must be 1000 character at most.";
    public static final String SELLER_SAVED = "Seller successfully saved.";
    public static final String THIS_PAGE_HAS_NOT_ANY_SELLER = "This page has not any sellers.";
    public static final String ANY_SELLER_HAS_NOT_ADDED_YET = "Any seller has not added yet.";
    public static final String DELETED_SELLERS_DO_NOT_EXIST = "Deleted seller does not exist yet.";
    public static final String SUCH_SELLER_ID_NOT_EXIST = "Such seller ID does not exist.";
    public static final String SELLER_UPDATED = "Seller data successfully updated.";
    public static final String SELLER_DELETED = "Seller successfully deleted.";
    public static final String SUCH_DELETED_SELLER_ID_NOT_EXIST = "Such deleted seller ID does not exist.";
    public static final String SELLER_RECOVERED = "Seller successfully recovered.";


    /*-------------======================== PRODUCT ========================-------------*/

    public static final String THIS_PRODUCT_OF_THIS_SELLER_TO_THIS_CATEGORY_ALREADY_IS_ADDED = "This product of this seller to this category already is added.";
    public static final String PRODUCT_SAVED = "Product successfully saved.";
    public static final String SUCH_PRODUCT_ID_NOT_EXIST = "Such product ID does not exist.";
    public static final String PRODUCT_MUST_HAVE_ATTACHMENT = "Product must have two attachment at least.";
    public static final String PRODUCT_UPDATED = "Product successfully updated.";
    public static final String PRODUCT_DELETED = "Product successfully deleted.";
    public static final String THIS_PAGE_HAS_NOT_ANY_SEARCHED_WORDS = "This page has not any searched words.";
    public static final String SEARCHED_WORDS_DELETED = "Searched words successfully deleted.";
    public static final String SUCH_SEARCHED_WORD_ID_NOT_EXIST = "Such ID of searched words does not exist.";
    public static final String SUCH_SESSION_ID_NOT_EXIST = "Such session ID does not exist.";
    public static final String IN_PAGE_NOT_ANY_PRODUCTS = "In %s page not any products.";

    /*-------------======================== MAIN-PANEL ========================-------------*/
    public static final String THIS_PANEL_IS_ALREADY_ADDED = "This panel already added.";
    public static final String NAME_OR_ATTACHMENT_MUST_BE_AT_LEAST = "Name or attachment must be at least.";
    public static final String SUCH_ATTACHMENT_ID_NOT_EXIST = "Such attachment ID does not exist.";
    public static final String IN_SAME_ORDER_ONLY_MAY_ADD_ATTACHMENTS = "In same order only may add attachments."; // so attachment + attachment in same order
    public static final String NEW_PANEL_SAVED = "New panel successfully saved.";
    public static final String ANY_PANEL_IS_NOT_ADDED_YET = "Any panel is not added yet.";
    public static final String SUCH_MAIN_PANEL_ID_NOT_EXIST = "Such main panel ID does not exist.";
    public static final String MAIN_PANEL_UPDATED = "Main panel successfully updated.";
    public static final String MAIN_PANEL_DELETED = "Main panel successfully deleted.";
    public static final String SUCH_METHOD_NAME_NOT_EXIST = "Such method name does not exist.";
    public static final String PANEL_CONNECTED_TO_METHOD = "Panel successfully connected to method.";
    public static final String ALL_PANELS_CONNECTED = "All panels connected to sellers, categories, products or methods.";
    public static final String ALL_PANELS_CONNECTED_WITH_METHODS = "All panels connected with methods.";
    public static final String PANELS_IS_NOT_CONNECTED_TO_METHODS_YET = "Panels is not connected to methods yet.";
    public static final String SUCH_CONNECTION_ID_NOT_EXIST = "Such connection ID does not exist.";
    public static final String CONNECTION_UPDATED = "Connection successfully updated.";
    public static final String CONNECTION_DELETED = "Connection successfully deleted.";

    /*-------------======================== USERS ========================-------------*/
    public static final String THIS_PHONE_NUMBER_BELONGS_TO_ANOTHER_USER = "This phone number belongs to another user or If it is you, you need to login.";
    public static final String THIS_EMAIL_BELONGS_TO_ANOTHER_USER = "This email belongs to another user, but this employee saved. PLease change his email.";
    public static final String PASSWORD_MUST_BE_8_CHARACTER_AT_LEAST = "Password must be 8 character at least";
    public static final String PASSWORD_MUST_CONTAIN_ONE_UPPERCASE_ONE_LOWERCASE_ONE_DIGIT_AT_LEAST = "Password must contain one uppercase, one lowercase and one digit at least.";
    public static final String PHONE_NUMBER_MUST_BE_13_CHARACTER = "Phone number must be 13 character.";
    public static final String ENTER_PHONE_NUMBER_WHICH_IS_REGISTERED_IN_UZB = "Please, enter phone number which is registered in Uzbekistan.";
    public static final String ENTER_ONLY_NUMBERS_AS_PHONE_NUMBER = "Please, enter only numbers as phone number.";
    public static final String THIS_CODE_WRONG = "This code is wrong.";
    public static final String BUYER_ADDED = "New buyer successfully saved.";
    public static final String SEND_SMS_REGISTER = "Hey Bro, your verification code is %s to register at GrapeBank. Please, hurry up, otherwise verification code will expire in 3 minute.";
    public static final String SEND_SMS_LOGIN = "Hey Bro, your verification code is %s to login at GrapeBank. Please, hurry up, otherwise verification code will expire in 3 minute.";
    public static final String THIS_CODE_EXPIRED = "This code expired.";
    public static final String YOU_REGISTERED_AS_EMPLOYEE_AT_SYSTEM_ETC = "You registered as employee at system and resigned, if you want to register as user, you must register with another number.";
    public static final String THIS_PHONE_NUMBER_NOT_FOUND = "This phone number not found.";
    public static final String CONFIRMATION_CODE_SENT = "Confirmation code sent.";
    public static final String PASSWORD_IS_WRONG = "Password is wrong.";
    public static final String THIS_TOKEN_IS_WRONG = "This token is wrong.";
    public static final String TOKEN_EXPIRED = "This token expired.";
    public static final String EMAIL_ALREADY_CONFIRMED = "Email already confirmed.";
    public static final String THIS_CODE_ALREADY_CONFIRMED = "This code already confirmed.";
    public static final String EMAIL_CONFIRMED = "Email successfully confirmed.";
    public static final String TOO_MANY_ATTEMPTS_ETC = "Too many attempts. Please, try after 15 minutes.";
    public static final String NEW_CODE_SENT = "New code sent.";
    public static final String YOU_DELETED_IN_SYSTEM = "You is deleted in system, so you can't enter to system. Please, don't try to enter again, because you can't anyway.";
    public static final String YOU_DELETED_IN_SYSTEM_ETC = "You is deleted in system or you changed your number, if you is deleted, please don't try again, because you can't enter anyway. If you changed your number, then you need to login again.";
    public static final String SUCH_USER_ID_NOT_EXIST = "Such user ID does not exist.";
    public static final String SEND_SMS_TO_CHANGE_PHONE_NUMBER = "Hey Bro, your verification code is %s to change your number. Please, hurry up otherwise verification code will expire in 3 minute. ";
    public static final String BUYER_UPDATED = "Buyer successfully updated.";
    public static final String BUYER_BLOCKED = "Buyer successfully blocked.";
    public static final String BUYER_UNBLOCKED = "Buyer successfully unblocked.";
    public static final String PHONE_NUMBER_UPDATED = "Phone number successfully updated.";
    public static final String PASSWORD_REQUIRED = "Password required.";
    public static final String EMPLOYEE_SAVED = "Employee successfully saved.";
    public static final String NAME_OR_PHONE_NUMBER_REQUIRED = "Name or phone number required.";
    public static final String EMPLOYEE_UPDATED = "Employee successfully updated.";
    public static final String PHONE_NUMBER_WRONG = "Phone number is wrong.";
    public static final String PHONE_NUMBER_ALREADY_CHANGED = "Phone number already changed.";
    public static final String EMPLOYEE_DELETED = "Employee successfully deleted.";
    public static final String EMPLOYEE_RESTORED = "Employee successfully restored.";
    public static final String CARD_NUMBER_MUST_CONTAIN_ONLY_DIGITS = "Card number must contain only digits.";
    public static final String CARD_NUMBER_LENGTH_MUST_BE_16_DIGITS = "Card number must contain only 16 digits.";
    public static final String CARD_EXPIRED_DATE_MUST_BE_4_DIGITS = "Card expired date must contain only 4 digits.";
    public static final String CARD_EXPIRED_DATE_MUST_CONTAIN_ONLY_DIGITS = "Card expired date must contain only digits.";
    public static final String ROLE_NAME_IS_WRONG_ETC = "Role name is wrong, please enter correctly role name!";
    public static final String EMPLOYEE_SALARY_CAN_NOT_BE_ZEO_OR_MINUS = "Employee salary can not be zero or minus.";
    /*-------------======================== COMMENT ========================-------------*/
    public static final String YOU_JUST_ADDED_THIS_COMMENT = "You just added this comment.";
    public static final String COMMENT_SAVED = "New comment successfully saved.";
    public static final String IN_PAGE_NOT_ANY_COMMENTS = "In %s page not any comments.";
    public static final String SUCH_COMMENT_ID_NOT_EXIST = "Such comment ID does not exist.";
    public static final String COMMENT_REPLIED = "Comment successfully replied.";
    public static final String COMMENT_UPDATED = "Comment successfully updated.";
    public static final String ONLY_TO_BUYER_COMMENTS_POSSIBLE_WRITE = "Replied comment only possible be written to Buyer's comment, not to replied comment.";
    public static final String COMMENT_DELETED = "Comment successfully deleted.";

    /*-------------======================== VIEWED PRODUCTS ========================-------------*/
    public static final String NEW_VIEWED_PRODUCT_ADDED = "New viewed product successfully added.";
    public static final String THIS_BUYER_HAS_NOT_SEEN_ANY_PRODUCTS = "This buyer has not seen any products yet.";
    public static final String IN_PAGE_NOT_ANY_VIEWED_PRODUCTS = "In %s page not any viewed products.";

    /*-------------======================== REGION ========================-------------*/
    public static final String THIS_REGION_HAS_BEEN_ALREADY_ADDED = "This region has been already added.";
    public static final String REGION_ADDED = "Region successfully added.";
    public static final String REGIONS_HAVE_NOT_BEEN_ADDED_YET = "Regions have not been added yet.";
    public static final String THIS_REGION_ADDED_AND_DELETED_ETC = "This region added and deleted, if you really want to add this region, then activate instead of adding this deleted region.";
    public static final String SUCH_REGION_ID_NOT_EXIST = "Such region ID does not exist.";
    public static final String REGION_UPDATED = "Region successfully updated.";
    public static final String REGION_DELETED = "Region successfully deleted.";
    public static final String THIS_REGION_CONNECTED_BRANCH_ETC = "This region connected branch and branch maybe connected order, that is why you can't completely delete this region, if you wanna delete, then deactivate it.";
    public static final String REGION_COMPLETELY_DELETED = "Region completely deleted.";

    /*-------------======================== BRANCH ========================-------------*/
    public static final String THIS_BRANCH_ALREADY_ADDED = "This branch already added to this region.";
    public static final String BRANCH_ADDED = "Branch successfully added.";
    public static final String BRANCHES_HAVE_NOT_BEEN_ADDED_YET = "Branches have not been added to this region yet.";
    public static final String SUCH_BRANCH_ID_NOT_EXIST = "Such branch ID does not exist.";
    public static final String BRANCH_UPDATED = "Branch successfully updated.";
    public static final String BRANCH_DELETED = "Branch successfully deleted.";
    public static final String THIS_BRANCH_HAS_ORDERS = "This branch has orders, so you can't completely delete this branch. If you wanna delete, deactivate it.";
    public static final String BRANCH_COMPLETELY_DELETED = "Branch completely deleted.";
    /*-------------======================== BASKET ========================-------------*/
    public static final String THIS_PRODUCT_ALREADY_ADDED_TO_BASKET = "This product already added to basket.";
    public static final String PRODUCT_ADDED_TO_BASKET = "Product successfully added to basket.";
    public static final String THERE_IS_NO_ANY_PRODUCTS_THIS_BUYERS_BASKET = "There is not any products in this buyer's basket.";
    public static final String SUCH_BASKET_ID_NOT_EXIST = "Such basket ID does not exist.";
    public static final String YOU_CANT_CHANGE_THIS_BASKET_ETC = "You can't change this basket, because this basket already ordered.";
    public static final String AMOUNT_OF_PRODUCT_MUST_NOT_BE_ZERO_OR_MINUS = "Amount of product must not be zero or minus.";
    public static final String YOU_MUST_ADD_THIS_PRODUCT_BEFORE_ETC = "You must add this product to basket before changing its amount.";
    public static final String NOT_ENOUGH_TO_THIS_AMOUNT = "This product %1$s left. Not enough to your amount, so you can order maximum %1$s product.";
    public static final String BASKET_UPDATED = "Basket updated.";
    public static final String YOU_CANT_DELETE_THIS_BASKET_ETC = "You can't delete this basket, because this basket already ordered.";
    public static final String YOU_MUST_ADD_THIS_PRODUCT_BEFORE_DELETING_IT = "You must add this product to basket before deleting it.";
    public static final String PRODUCT_DELETED_FROM_BASKET = "Product successfully deleted from basket.";
    /*-------------======================== ORDER ========================-------------*/
    public static final String SUCH_PLACE_NOT_FOUND_ETC = "Such place not found, place again enter place name.";
    public static final String CLIENT_ERROR = "Client error, exception while entering.";
    public static final String HOME_LATITUDE_AND_LONGITUDE_MUST = "If you want GrapeBank delivered to your home, you must enter your home location. ";
    public static final String GRAPE_BANK_SERVICES_ONLY_IN_UZBEKISTAN = "GrapeBank services only in Uzbekistan, Please select delivering place in Uzbekistan.";
    public static final String REVERSE_GEOCODING_API_RESPONSE_ERROR = "Reverse geocoding api response error.";
    public static final String ROUTE_MATRIX_API_RESPONSE_ERROR = "Route matrix api response error.";
    public static final String THIS_BUYER_HAS_NOT_ANY_PRODUCTS_IN_HIS_BASKET = "This buyer has not any products in his basket.";
    public static final String ORDER_ADDED = "Order successfully added";
    public static final String HEY_UI_DEVELOPER_SEND_ME_ETC = "Hey UI developer send me parameter for time only this format allTime; latestWeek; day,month,year; month,year; year. Don't send me anything else for this API parameter.";
    public static final String SUCH_ORDER_ID_NOT_EXIST = "Such order ID does not exist.";
    public static final String IN_PAGE_NOT_ANY_ORDERS = "In %s page does not any orders.";
    public static final String NOTIFICATION_ORDER_PREPARING_EXTREMELY_FIRE = "Hey Admin, We have extremely fire notification that Region ID:%1$s  Region name:%2$s, Branch ID:%3$s  Branch name:%4$s, Order ID:%5$s is delaying to prepare to three days. Right now connect with them and explain them to fastly do perform order.";
    public static final String NOTIFICATION_ORDER_PREPARING_FIRE = "Dear Admin, We have fire notification that Region ID:%1$s  Region name:%2$s, Branch ID:%3$s  Branch name:%4$s, Order ID:%5$s is delaying to prepare to two days. Please connect with them as soon as possible.";
    public static final String NOTIFICATION_ORDER_PREPARING_WARNING = "Dear Admin, We have warning notification that Region ID:%1$s  Region name:%2$s, Branch ID:%3$s  Branch name:%4$s, Order ID:%5$s is delaying to prepare to one day. Please when you have free time connect with them.";
    public static final String DELAYING_TO_PREPARE = "DELAYING_TO_PREPARE";
    public static final String DELAYING_TO_DELIVER = "DELAYING_TO_PREPARE";
    public static final String NOTIFICATION_ORDER_DELIVERING_WARNING = "Dear Admin, We have warning notification that Region ID:%1$s  Region name:%2$s, Branch ID:%3$s  Branch name:%4$s, Order ID:%5$s is delaying to delivery to one day. Please when you have free time connect with them or deliver.";
    public static final String NOTIFICATION_ORDER_DELIVERING_FIRE = "Dear Admin, We have fire notification that Region ID:%1$s  Region name:%2$s, Branch ID:%3$s  Branch name:%4$s, Order ID:%5$s is delaying to delivery to two days. Please connect with them or deliver as soon as possible.";
    public static final String NOTIFICATION_ORDER_DELIVERING_EXTREMELY_FIRE = "Hey Admin, We have extremely fire notification that Region ID:%1$s  Region name:%2$s, Branch ID:%3$s  Branch name:%4$s, Order ID:%5$s is delaying to delivery to three days. Right now connect with them or deliver to explain them to fastly do perform delivering .";
    public static final String NOTIFICATION_ORDER_WAITING_CLIENT_FIRST_DAY = "Good Afternoon, Dear %1$s %2$S. Order ID: %3$s. Your order is ready and waiting you at %4$s branch. When you have a bunch of free time, you can take away it. Sincerely GrapeBank";
    public static final String NOTIFICATION_ORDER_WAITING_CLIENT_SECOND_DAY = "Good Afternoon, Dear %1$s %2$S. Order ID: %3$s. Your order has been waiting you for two days at %4$s branch. When you have a bunch of free time, you can take away it. Sincerely GrapeBank.";
    public static final String NOTIFICATION_ORDER_WAITING_CLIENT_THIRD_DAY = "Good Afternoon, Dear %1$s %2$S. Order ID: %3$s. Your order has been waiting you for three days at %4$s branch. Hopefully you don't forget it. You can take away it anytime. Sincerely GrapeBank.";
    public static final String NOTIFICATION_ORDER_WAITING_CLIENT_FOURTH_DAY = "Good Afternoon, Dear %1$s %2$S. Order ID: %3$s. Your order has been waiting you for four days at %4$s branch. The order is about to expire, we ask you to hurry. You can take away it anytime. Sincerely GrapeBank.";
    public static final String NOTIFICATION_ORDER_WAITING_CLIENT_FIFTH_DAY = "Good Afternoon, Dear %1$s %2$S. Order ID: %3$s. Your order has been waiting you for five days at %4$s branch. %5$s your order will automatically been canceled. If you try to take away it right now, you may catch up, so hurry up. Sincerely GrapeBank.";
    public static final String NOTIFICATION_ORDER_RETURN = "Good Afternoon, Dear %1$s %2$S. Order ID:%3$s. Your order has just been canceled. It's okay if you couldn't get it this time, when you have a bunch of free time then order to branch or to house. Sincerely GrapeBank.";
    public static final String NOTIFICATION_ORDER_RETURN_WITH_CARD = "Good Afternoon, Dear %1$s %2$S. Order ID:%3$s. Your order has just been canceled and your money has returned to your card. Please check out your card money. It's okay if you couldn't get it this time, when you have a bunch of free time then order to branch or to house. Sincerely GrapeBank.";
    public static final String YOU_CANT_CHANGE_THIS_ORDER_STATUS_TO_DELIVERING_ETC = "You can't change status of this order to Delivering, because order status must be Preparing to in order to change to Delivering.";
    public static final String YOU_CANT_CHANGE_THIS_ORDER_STATUS_TO_WAITING_CLIENT_ETC = "You can't change status of this order to Waiting Client, because order status must be Delivering to in order to change to Waiting Client.";
    public static final String YOU_CANT_CHANGE_THIS_ORDER_STATUS_TO_SOLD_ETC = "You can't change status of this order to Sold, because order status must be Delivering or Waiting Client to in order to change to Sold.";
    public static final String YOU_CANT_CHANGE_THIS_ORDER_STATUS_TO_RETURNED_ETC = "You can't change status of this order to Returned, because order status must be Delivering or Waiting Client or Sold to in order to change to Returned.";
    public static final String SUCH_ORDER_STATUS_NOT_EXIST = "Such order status does not exist. Wrong status:%s";
    public static final String ORDER_STATUS_ALREADY_CHANGED_TO_THIS_STATUS = "Order status already changed to this status.";
    public static final String CLIENT_CANT_CANCEL_THIS_ORDER_ETC = "Client can't cancel this order, because order return period 7 days and already return period expired.";
    public static final String ORDER_STATUS_UPDATED = "Order status successfully updated.";
    public static final String YOU_CANT_CHANGE_ORDER_ETC = "You can't change this order anymore, because order status is %s, if order status is preparing or delivering, then you can change it.";
    public static final String ORDER_UPDATED = "Order successfully updated.";
    /*-------------======================== CHAT ========================-------------*/
    public static final String TEXT_OR_ATTACHMENT_IS_REQUIRED = "Text or attachment is required.";
    public static final String MESSAGE_ADDED = "Message successfully added";
    public static final String SUCH_MESSAGE_ID_NOT_EXIST = "Such message ID does not exist.";
    public static final String CHAT_ALREADY_BLOCKED = "Chat already blocked, so you can't add, change or delete any messages in this chat any more.";
    public static final String MESSAGE_OF_CHAT_ONLY_CAN_BE_CHANGED_BY_OPERATOR_ETC = "Message of chat only can be changed by operator or buyer who is written it.";
    public static final String MESSAGE_UPDATED = "Message successfully updated.";
    public static final String MESSAGE_DELETED = "Message successfully deleted.";
    public static final String NO_CONVERSATION_IN_THIS_CHAT_YET = "No conversations in this chat yet.";
    public static final String SUCH_CHAT_ID_NOT_EXIST = "Such chat ID does not exist.";
    public static final String OK = "OK";
    public static final String NO_ANY_UNREAD_CHATS = "No any unread chats.";
    public static final String NO_ANY_CONVERSATIONS_IN_ALL_CHATS = "No any conversations in all chats.";
    public static final String THIS_CHAT_NAME_ALREADY_EXIST = "This chat name already exist.";
    public static final String CHAT_UPDATED = "Chat successfully updated.";
    /*-------------======================== FAVOURITE ========================-------------*/

    public static final String THIS_PRODUCT_ALREADY_ADDED_TO_FAVOURITE_PRODUCT_LIST = "This product already added to favourite product list.";
    public static final String PRODUCT_ADDED_TO_FAVOURITE_PRODUCT_LIST = "Product successfully added to favourite product list.";
    public static final String PRODUCT_DELETED_FROM_FAVOURITE_LIST = "Product successfully deleted from favourite product list.";
    public static final String THIS_PRODUCT_NOT_EXIST_IN_YOUR_FAVOURITE_LIST = "This product does not exist in your favourite product list.";
    /*-------------======================== FAQ ========================-------------*/

    public static final String THIS_QUESTION_ALREADY_ADDED = "This message already added.";
    public static final String THIS_ANSWER_ADDED_FOR_ANOTHER_QUESTION = "This answer added for another question.";
    public static final String SUCH_PARENT_FAQ_ID_NOT_EXIST = "Suc parent FAQ ID does not exist.";
    public static final String FAQ_SAVED = "FAQ successfully saved.";
    public static final String SUCH_FAQ_ID_NOT_EXIST = "Such FAQ ID does not exist.";
    public static final String THIS_QUESTION_OR_ANSWER_ALREADY_ADDED = "This question or answer already added.";
    public static final String FAQ_UPDATED = "FAQ successfully updated.";
    public static final String FAQ_DELETED = "FAQ successfully deleted.";
    /*-------------======================== EXTERNAL SERVICES ========================-------------*/
    public static final String FOR_EXCHANGING_CURRENCIES_MUST_NOT_BE_NULL_FROM_AND_TO = "FROM and TO must not be null for exchanging currencies, so please send available currency abbreviation.";
    public static final String CURRENCY_AMOUNT_MUST_BE_GREATER_THAN_ZERO = "Currency amount must be greater than zero.";
    public static final String IS_EQUAL = "%1$s %2$s is equal to %3$s %4$s at %5$s";
    public static final String CLIENT_INPUT_ERROR = "Client input error. So client entered wrong currency abbreviation or not existing date.";
    public static final String WEATHER_INTEGRATION_RESPONSE_ERROR = "Weather integration response error.";
    public static final String CLIENT_WEATHER_INPUT_ERROR = "You entered wrong location name, please again enter location name correctly.";

    public static final String GENDER_NAME_IS_WRONG_ETC = "Gender name is wrong, please enter gender name is correctly.";
    public static final String ADMIN_YOU_CANT_ADD_EMPLOYEE_ETC = "Dear Admin, you can't add new employee that his role name is Director, Admin or Buyer!";
    public static final String YOU_CANT_SEE_INFORMATION_OF_ANOTHER_EMPLOYEE = "Hey bro, you can't see information of another employee. You can see only your information.";
    public static final String DEAR_ADMIN_YOU_CANT_SEE_DIRECTOR_ADMIN_ETC = "Dear Admin. You can't see Director's and other admins' information, so you can see all of left employees.";
    public static final String ENTER_VALID_PHONE_OR_NAME = "Enter valid phone number and name, these values that you entered aren't available.";
    public static final String ADMIN_YOU_CANT_CHANGE_INFORMATION_OF_ANOTHER_ADMIN = "Dear %s. You can't change information of another admin.";
    public static final String ADMIN_YOU_CANT_CHANGE_INFORMATION_OF_DIRECTOR = "Dear %s, You can't change information of Director.";
    public static final String YOU_CANT_CHANGE_INFORMATION_OF_ANOTHER_EMPLOYEE = "Dear %s, you can't change information of another employee.Please, don't do that again, because you can't anyway.";
    public static final String ADMIN_YOU_CANT_DELETE_EMPLOYEE_FROM_SYSTEM = "Dear %s, you can't delete Director and Admins, so you can delete all of left employees.";
    public static final String ADMIN_YOU_CANT_RECOVER_EMPLOYEE_TO_SYSTEM = "Dear %s, you can't recover Director and Admins, so you can recover all of left employees.";
    public static final String THIS_USER_NOT_FOUND_OR_THIS_WASNT_DELETED = "This user not found, or this user wasn't deleted.";
    public static final String MESSAGE_FOR_SMS = "Dear %1$s, Money isn't enough to pay employees salaries. Please, right now connect with %2$s to resolve this problem. Amount of employees that aren't paid salaries is %3$s . Amount of money to be paid is %4$s";
    public static final String NOTIFICATION_MESSAGE = "Amount of employee that aren't paid salaries: %1$s . Amount of money to be paid: %2$S";
    public static final String THIS_PHONE_NUMBER_REGISTERED_ALREADY = "This phone number registered already. You need to login.";
    public static final String YOU_CANT_SEE_INFORMATION_OF_ANOTHER_BUYER = "Dear %1$s %2$s, you can't see information of another buyer.";
    public static final String THIS_NUMBER_REGISTERED_AS_BUYER = "This number registered as buyer, so you can't register as employee via this phone number.";
    public static final String THIS_NUMBER_REGISTERED_AS_EMPLOYEE = "This phone number registered as employee, so you can't register as buyer via this phone number.";
    public static final String YOU_CANT_EDIT_INFORMATION_OF_ANOTHER_BUYER = "Dear %1$s %2$s, you can't edit information of another buyer. Please don't do that again!";
    public static final String SUCH_DELETED_USER_ID_NOT_EXIST = "Such deleted user ID does not exist.";
    public static final String CARD_DETAILS_REQUIRED_ETC = "Card details must have. So, please enter card number and card expire date.";
    public static final String ENTER_1000_UZS_AT_LEAST = "Please, enter 1000 UZS at least. Money less than 1000 UZS can not enter.";
    public static final String BALANCE_IS_NOT_ENOUGH_IN_YOUR_CARD = "Balance is not enough in your card.";
    public static final String YOUR_BALANCE_FILLED = "Your balance successfully filled.";
    public static final String YOU_CANT_FILL_ANOTHER_BUYER_BALANCE = "Dear %1$s %2$s, you can't fill another buyer's balance. Please, don't try to do that again!";
    public static final String ATTACHMENTS_DO_NOT_EXIST_IN_THIS_PAGE = "Attachments do not exist in this page.";
    public static final String RESTORE_PASSWORD_MESSAGE = "Have you forgotten your password? It's okay. You need to enter verification code that is sent on SMS in order to log in. Then you can easily change your password on your account. Verification code: %s . Verification code will expire in 3 minutes.";
    public static final String VERIFICATION_CODE_HAS_BEEN_SENT = "Your verification code has been sent to your phone and email, please check out your email or phone.";
    public static final String THIS_EMPLOYEE_IDS_ARE_UNAVAILABLE_ETC = "This employee IDs are unavailable, please enter correctly employee IDs that its role is SELLER.";
    public static final String YOU_CANT_SEE_INFORMATION_OF_ANOTHER_SELLER = "Hey %1$s %2$s, you can't see information of another seller that its owner isn't you.";
    public static final String YOU_CANT_EDIT_INFORMATION_OF_ANOTHER_SELLER = "Hey %1$s %2$s, you can't see information of another seller that its owner isn't you.";
    public static final String PRODUCT_PRICE_LIST_SIZE_MUST_BE_2 = "Product price list size must be 2 and first index value that is fromPrice need to be less than second index value that is toPrice.";
    public static final String THIS_PANEL_HAS_NOT_BEEN_CONNECTED_WITH_ANYTHING = "This panel has not been connected with anything";
    public static final String THIS_CATEGORY_NOT_GRAND_CATEGORY_ETC = "This category is not grand category, please enter correctly grand category ID.";
    public static final String PRODUCT_MUST_HAVE_2_AVAILABLE_ATTACHMENTS = "Product must have 2 available attachments, Please enter correctly attachment IDs for product.";
    public static final String THIS_ATTACHMENT_ALREADY_ADDED_TO_ANOTHER_PRODUCT = "This attachments already added to another product. Already added attachment IDs: %s";
    public static final String THIS_PANEL_ALREADY_CONNECTED_WITH_SOMETHING = "This panel already connected with other %s, so if you wanna connect it with something, you firstly need to disconnect panel from anything, and after that you can it.";
    public static final String MAIN_PANEL_CONNECTED_TO_SELLER = "Main panel successfully connected to seller.";
    public static final String MAIN_PANEL_CONNECTED_TO_CATEGORY = "Main panel successfully connected to category.";
    public static final String MAIN_PANEL_CONNECTED_TO_PRODUCT = "Main panel successfully connected to product.";
    public static final String THIS_METHOD_ALREADY_CONNECTED_WITH_ANOTHER_PANEL = "This method already connected with another panel.";
    public static final String THIS_CATEGORY_ALREADY_CONNECTED_WITH_ANOTHER_PANEL = "This category already connected with another panel.";
    public static final String THIS_SELLER_ALREADY_CONNECTED_WITH_ANOTHER_PANEL = "This seller already connected with another panel.";
    public static final String THIS_PRODUCT_ALREADY_CONNECTED_WITH_ANOTHER_PANEL = "This product already have connected with another panel.";
    public static final String ANY_PANELS_NOT_CONNECTED = "Any panels have not connected.";
    public static final String YOU_CANT_SEE_COMMENTS_OF_ANOTHER_BUYER = "Dear %1$s %2$s, you can't see another buyer's comments. Please don't try again to see.";
    public static final String YOU_CANT_SEE_UNANSWERED_COMMENTS_OF_ANOTHER_SELLER = "Dear %1$s %2$s, you can't see unanswered comments of another seller's product.";
    public static final String YOU_CANT_REPLY_TO_THIS_COMMENT_ETC = "Dear %1$s %2$s, you can't reply to this comment, because product of this comment doesn't belong you!";
    public static final String THIS_ATTACHMENTS_NOT_AVAILABLE = "This attachments aren't available.";
    public static final String YOU_CANT_EDIT_DELETE_THIS_COMMENT = "Dear %1$s %2$s, you can't edit, delete this comment, because it doesn't belong to you.";
    public static final String YOU_CANT_EDIT_ANOTHER_BUYERS_COMMENT = "Dear %1$s %2$s, you can't edit, delete another buyer's comment. Please don't try to do that again.";
    public static final String YOU_CAN_EDIT_ONLY_REPLIED_COMMENTS = "Dear %1$s %2$s, you can edit, only replied comments.";
    public static final String YOU_CANT_EDIT_DELETE_COMMENTS_OF_ANOTHER_SELLER = "Dear %1$s %2$s, you can't edit, delete comments of another employee.";
    public static final String AMOUNT_OF_STARS_CAN_NOT_BE_NULL_ETC = "Amount of stars can not be null. It must be between 1 and 5.";
    public static final String YOU_CANT_ADD_COMMENT_TO_THIS_PRODUCT = "Dear %1$s %2$s, you can't add comment to this product as employee, because this product doesn't belong to you.";
    public static final String ANY_COMMENTS_ADDED_TO_THIS_PRODUCT = "Any comments added to this product yet.";
    public static final String THIS_BUYER_HAS_NOT_WRITTEN_ANY_COMMENTS_YET = "This buyer has not written any comments yet.";
    public static final String THIS_PRODUCT_HAS_NOT_ANY_UNANSWERED_COMMENTS = "This product has not any unanswered comments.";
    public static final String ANY_PRODUCTS_HAVE_NOT_SEEN_YET = "Any products have not seen yet.";
    public static final String SESSION_ID_OR_BUYER_ID_REQUIRED ="Session ID or buyer ID required." ;
    public static final String THIS_BUYER_HAS_NOT_ADDED_ANY_PRODUCTS_TO_BASKET_YET = "This buyer has not added any products to basket yet.";
    public static final String YOU_CANT_SEE_PRODUCTS_OF_ANOTHER_BUYERS_BASKET = "Hey bro, you can't see products in another buyer's basket. Please don't try to do that again.";
    public static final String CLIENT_INPUT_ERROR_OR_API_IP_SERVER_ERROR = "Client input ip error or api ip server error.";
    public static final String GEOAPIFY_SERVER_ERROR = "Geoapify server error.";
    public static final String API_IP_SERVER_ERROR = "Api server error.";
    public static final String YOU_CANT_SEE_BUYER_BASKET_PRODUCTS = "Hey you. You can't see buyer basket products, because it may not belong you.";
    public static final String YOU_CANT_SEE_ANOTHER_BUYER_BASKET = "Hey bro, you can't see another buyer's basket.";
    public static final String YOU_CANT_SEE_BUYER_BASKET_PRODUCTS_AS_EMPLOYEES = "Dear %1$s %2$s, you can't see buyer basket products.";
    public static final String THIS_BASKET_DOES_NOT_BELONG_YOU = "This basket does not belong you, so you can't see it.";
    public static final String YOU_CANT_PAY_USING_CARD_ETC = "You can't pay using card because you don't have any cards. If you wanna pay using card, you need to enter card details.";
}