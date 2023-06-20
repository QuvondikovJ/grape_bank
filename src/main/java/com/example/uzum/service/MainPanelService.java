package com.example.uzum.service;

import com.example.uzum.dto.mainPanel.MainPanelDTO;
import com.example.uzum.dto.Result;
import org.springframework.stereotype.Service;

@Service
public interface MainPanelService {


    Result<?> add(MainPanelDTO mainPanelDto);

    Result<?> getAll();

    Result<?> edit(Integer id, MainPanelDTO mainPanelDto);

    Result<?> getById(Integer id);

    Result<?> getOrdersCount();

    Result<?> delete(Integer id);

    Result<?> connectPanelsToMethods(String panelId, String methodName);

    Result<?> getMethodNamesToAddPanel();

    Result<?> getUnconnectedPanels();

    Result<?> disconnectPanel(Integer id);

    Result<?> connectPanelsToSellers(String panelId, String sellerId);

    Result<?> connectPanelsToCategories(String panelId, String categoryId);

    Result<?> connectPanelsToProducts(String panelId, String productId);

    Result<?> getConnectedPanels();
}
