package com.starchain.constants;

/**
 * @author
 * @date 2024-12-19
 * @Description Pacificpay银行卡对接API
 */
public class CardUrlConstants {
    // 创建持卡人
    public static final String addCardHolder = "/apiMerchant/addCardHolder";

    // 修改持卡人
    public static final String editCardHolder = "/apiMerchant/editCardHolder";

    // 创建卡
    public static final String addCard = "/apiMerchant/addCard";

    // 查询商户余额
    public static final String mchInfo = "/apiMerchant/mchInfo";

    // 查询商户交易明细
    public static final String tradeDetail = "/apiMerchant/tradeDetail";

    // 修改卡限额
    public static final String updateLimit = "/apiMerchant/updateLimit";

    // 卡充值
    public static final String applyRecharge = "/apiMerchant/applyRecharge";

    // 卡提现
    public static final String applyWithdraw = "/apiMerchant/applyWithdraw";

    // 锁定卡
    public static final String lockCard = "/apiMerchant/lockCard";

    // 解锁卡
    public static final String unlockCard = "/apiMerchant/unlockCard";

    // 查询卡
    public static final String getCardDetail = "/apiMerchant/getCardDetail";

    //申请销卡
    public static final String deleteCard = "/apiMerchant/deleteCard";

    //申请换卡
    public static final String changeCard = "/apiMerchant/changeCard";

    public static final String APPID = "m7567910-0cbf-423b-a39f-47fd418bac8g";

    public static final String PUBLICKEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEArr6raOoFx4fe3KYk7238DvSlfPAEQC5I88aj+5ZlZT6EW0imvCVWZaEWKHDyB2JFXOKOsrZNM+Z9j/Wk1if7AN9e+QTfNSVc0BT+qZ1hCtITnKUFdGmak6DDQpeuT5oAnoPzlhR7cCoaZbbnv+ZWqZ0nbI8Zn6LbF8MoJOuzLyjcAJvx8FGD8s2m9cSEf4o4+GX9eaXgH/6PvSagzZySjfil9nW1/UEKdLLVssF9GrTvUsOeqofVCmNUpIJLOkK2Vew86Aqja2JdnOrcovlF20EHC9y+1IpHHS3gIeVinimafMMenMlgB7uIfAFzXo1/swRPwZ5ofiIs9BixqeHJhwIDAQAB";

    public static final String PRIVATEKEY = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCuvqto6gXHh97cpiTvbfwO9KV88ARALkjzxqP7lmVlPoRbSKa8JVZloRYocPIHYkVc4o6ytk0z5n2P9aTWJ/sA3175BN81JVzQFP6pnWEK0hOcpQV0aZqToMNCl65PmgCeg/OWFHtwKhpltue/5lapnSdsjxmfotsXwygk67MvKNwAm/HwUYPyzab1xIR/ijj4Zf15peAf/o+9JqDNnJKN+KX2dbX9QQp0stWywX0atO9Sw56qh9UKY1Skgks6QrZV7DzoCqNrYl2c6tyi+UXbQQcL3L7UikcdLeAh5WKeKZp8wx6cyWAHu4h8AXNejX+zBE/Bnmh+Iiz0GLGp4cmHAgMBAAECggEATTvMuvQkVcpBwDhWvtRXwaxzsELNPii26bf0JnCUpCj7ivUTbLj79LM7/efdsVHH4oPoGijk+nl3KbeigVzuXbZcXae/l9fJq9Z7sC/1AjlTnbp6LLe4MGOvqbEsw9YHTAh6Eu0+mZaiUC6FQ5Xk29av5BVjd+EdAOWqypa6fa7N12lWlFSrb8lneDwHDL2EMdzDF3wfKcDk4DYtH8Gzor9zxPxeExZ43zTNZBJ4SKMGmfPHZ4KvXvtIoM5+RST6MMnFZu20/ur/vamYXkic/ttXc6MsTnbPGyJeo9583E6DmS+kaB8LqKHBNqI2q/yJDjO4mQ2RAC+SFU9DclkpkQKBgQD6hluImQs9DkFJ/MMSTn2X121rlySf427Yu8zz/bsR8voNbrhoADSoaytf2srnto9Wzxs49MkST9/VJbtLKGr7ROWeSaNE4+JIyPHAv0EaziEFclnE9Txm+oiuplXy8ByDkYfwv76PSZtUIJ2kBYk7Rk800mhWSBtJWIUI3HH3owKBgQCykFYHeO+huaQ2629ccYan0NZya2Tbks00KJTcHQLctefBBZc0JrAdear7zS6hdDzvRtBiyDKwZtAAtImaNOsXemjuZs2khTPW4sYPiP9/LPBL2PHkjvCRemSgD5qHv1f7rtbfPj8w5wPsqP38OZ0VeI+klmNtUJa3KalSxv9UzQKBgAnrdgZjoPlK7jTiyEqaRwjTI+cFthKKq+HzFbt3iYe9aj9L6gtcLrmYfMAv5qbkKKrUSoynzKpn9UH4W4EvWYUXhkwNOGTK7TPamQ/7wrUwAki37WwKFdkyBGV9+ptC/K4M/if/P6lmzbq4C+XpvjRVpV8/EZiRBFuUILhoGi8nAoGBAKCBRyMuXNAVS3umQxJoZW6hNLHiwOTx3ww8UhpM5Y644BnLya8x+2pzO/Nc6sZr8n3uPFAevhl8QQnxPiLj4FHZUUrCE6UHqqJEc13xeh+5qEG1PzGAHtPVnW+WtwPARfjQnwpKLIjrjsaC3NKEgyOSnCOIntTa9K6tfysN/uMdAoGAB48kPpG/8Sr78MWXq0hxPRAWByUhLF6NCnWt1vv3kpCzPDMD2+I9roQ74gXQ2d2cypqLCdhstF2AJUSRmyS2Iu47FK1q9R1LZZ7PBd3xZGLoU13WOMf1frR+csT9AkGR0eY8zwG7tFr/5kYT3FLOKcn/T5PZmOEKtWi09JcBnck=";

    public static String APPSECRET = "25e202f945f977e182b809dee87d831f";

    public static String BASEURL = "http://42.194.129.4:8082/merchant-app-server/mch";
}
