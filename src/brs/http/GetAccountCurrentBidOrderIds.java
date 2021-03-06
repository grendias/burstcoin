package brs.http;

import static brs.http.common.Parameters.ACCOUNT_PARAMETER;
import static brs.http.common.Parameters.ASSET_PARAMETER;
import static brs.http.common.Parameters.FIRST_INDEX_PARAMETER;
import static brs.http.common.Parameters.LAST_INDEX_PARAMETER;

import brs.BurstException;
import brs.Order;
import brs.db.BurstIterator;
import brs.services.ParameterService;
import brs.util.Convert;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONStreamAware;

import javax.servlet.http.HttpServletRequest;

public final class GetAccountCurrentBidOrderIds extends APIServlet.APIRequestHandler {

  private final ParameterService parameterService;

  GetAccountCurrentBidOrderIds(ParameterService parameterService) {
    super(new APITag[] {APITag.ACCOUNTS, APITag.AE}, ACCOUNT_PARAMETER, ASSET_PARAMETER, FIRST_INDEX_PARAMETER, LAST_INDEX_PARAMETER);
    this.parameterService = parameterService;
  }

  @Override
  JSONStreamAware processRequest(HttpServletRequest req) throws BurstException {

    long accountId = parameterService.getAccount(req).getId();
    long assetId = 0;
    try {
      assetId = Convert.parseUnsignedLong(req.getParameter("asset"));
    } catch (RuntimeException e) {
      // ignore
    }
    int firstIndex = ParameterParser.getFirstIndex(req);
    int lastIndex = ParameterParser.getLastIndex(req);

    BurstIterator<Order.Bid> bidOrders;
    if (assetId == 0) {
      bidOrders = Order.Bid.getBidOrdersByAccount(accountId, firstIndex, lastIndex);
    } else {
      bidOrders = Order.Bid.getBidOrdersByAccountAsset(accountId, assetId, firstIndex, lastIndex);
    }
    JSONArray orderIds = new JSONArray();
    try {
      while (bidOrders.hasNext()) {
        orderIds.add(Convert.toUnsignedLong(bidOrders.next().getId()));
      }
    } finally {
      bidOrders.close();
    }
    JSONObject response = new JSONObject();
    response.put("bidOrderIds", orderIds);
    return response;
  }

}
