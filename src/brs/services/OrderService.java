package brs.services;

import brs.Order.Ask;
import brs.Order.Bid;
import brs.db.BurstIterator;

public interface OrderService {

  Ask getAskOrder(long orderId);

  Bid getBidOrder(long orderId);

  BurstIterator<Ask> getAllAskOrders(int from, int to);

  BurstIterator<Bid> getAllBidOrders(int from, int to);

  BurstIterator<Bid> getSortedBidOrders(long assetId, int from, int to);
}
