package rise.http;

import rise.Account;
import rise.Attachment;
import rise.Constants;
import rise.DigitalGoodsStore;
import rise.RiseException;
import rise.util.Convert;
import org.json.simple.JSONStreamAware;

import javax.servlet.http.HttpServletRequest;

import static rise.http.JSONResponses.INCORRECT_DELTA_QUANTITY;
import static rise.http.JSONResponses.MISSING_DELTA_QUANTITY;
import static rise.http.JSONResponses.UNKNOWN_GOODS;

public final class DGSQuantityChange extends CreateTransaction {

    static final DGSQuantityChange instance = new DGSQuantityChange();

    private DGSQuantityChange() {
        super(new APITag[] {APITag.DGS, APITag.CREATE_TRANSACTION},
                "goods", "deltaQuantity");
    }

    @Override
    JSONStreamAware processRequest(HttpServletRequest req) throws RiseException {

        Account account = ParameterParser.getSenderAccount(req);
        DigitalGoodsStore.Goods goods = ParameterParser.getGoods(req);
        if (goods.isDelisted() || goods.getSellerId() != account.getId()) {
            return UNKNOWN_GOODS;
        }

        int deltaQuantity;
        try {
            String deltaQuantityString = Convert.emptyToNull(req.getParameter("deltaQuantity"));
            if (deltaQuantityString == null) {
                return MISSING_DELTA_QUANTITY;
            }
            deltaQuantity = Integer.parseInt(deltaQuantityString);
            if (deltaQuantity > Constants.MAX_DGS_LISTING_QUANTITY || deltaQuantity < -Constants.MAX_DGS_LISTING_QUANTITY) {
                return INCORRECT_DELTA_QUANTITY;
            }
        } catch (NumberFormatException e) {
            return INCORRECT_DELTA_QUANTITY;
        }

        Attachment attachment = new Attachment.DigitalGoodsQuantityChange(goods.getId(), deltaQuantity);
        return createTransaction(req, account, attachment);

    }

}
