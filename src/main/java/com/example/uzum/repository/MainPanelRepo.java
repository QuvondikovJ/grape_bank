package com.example.uzum.repository;

import com.example.uzum.entity.Attachment;
import com.example.uzum.entity.MainPanel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface MainPanelRepo extends JpaRepository<MainPanel, Integer> {

    boolean existsByNameEnAndIsActiveOrNameUzAndIsActive(String nameEn, Boolean isActive, String nameUz, Boolean isActive2);

    boolean existsByNameEnAndIsActiveAndIdNotOrNameUzAndIsActiveAndIdNot(String nameEn, Boolean isActive, Integer id, String nameUz, Boolean isActive2, Integer id2);


    @Query(value = "SELECT MAX(mainp.panelOrder) FROM MainPanel AS mainp WHERE mainp.isActive=TRUE")
    Integer getMaxOrder();

    @Transactional
    @Modifying
    @Query(value = "UPDATE MainPanel AS mainp SET mainp.panelOrder=mainp.panelOrder+1 WHERE mainp.panelOrder>=:order AND mainp.isActive=TRUE ")
    void updateOrderToAdd(Integer order);

    @Transactional
    @Modifying
    @Query(value = "UPDATE MainPanel AS mainp SET mainp.panelOrder=mainp.panelOrder-1 WHERE mainp.panelOrder>:order AND mainp.isActive=TRUE ")
    void updateOrderToDelete(Integer order);


    @Query(value = "SELECT mainp FROM MainPanel AS mainp WHERE mainp.panelOrder=:order")
    List<MainPanel> getMainPanelInThisOrder(Integer order);


    List<MainPanel> findAllByIsActiveOrderByPanelOrder(Boolean isActive);

    Optional<MainPanel> findByIdAndIsActive(Integer id, Boolean isActive);

    @Transactional
    @Modifying
    @Query(value = "UPDATE MainPanel AS mainp SET mainp.panelOrder=mainp.panelOrder-1 WHERE mainp.panelOrder>:oldOrder AND mainp.panelOrder<=:newOrder AND mainp.isActive=TRUE ")
    void updateOrderFromOldOrder(Integer oldOrder, Integer newOrder);

    @Transactional
    @Modifying
    @Query(value = "UPDATE MainPanel AS mainp SET mainp.panelOrder=mainp.panelOrder+1 WHERE mainp.panelOrder>=:newOrder AND mainp.panelOrder<:oldOrder AND mainp.isActive=TRUE ")
    void updateOrderToOldOrder(Integer oldOrder, Integer newOrder);


    @Query(value = "SELECT mpanel FROM MainPanel AS mpanel WHERE mpanel.id NOT IN (SELECT pcmethod.mainPanel.id FROM PanelConnectMethod AS pcmethod)")
    List<MainPanel> getPanelsNotUsedWithMethods();

    @Query(value = "SELECT mp FROM MainPanel AS mp WHERE mp.id IN (SELECT sel.mainPanel.id FROM Seller AS sel WHERE sel.isActive=TRUE) " +
            " OR mp.id IN (SELECT ctg.mainPanel.id FROM Category AS ctg WHERE ctg.isActive=TRUE ) " +
            " OR mp.id IN (SELECT pro.mainPanel.id FROM Product AS pro WHERE pro.isActive=TRUE ) " +
            " OR mp.id IN (SELECT pm.mainPanel.id FROM PanelConnectMethod AS pm) AND mp.isActive=TRUE ")
    List<MainPanel> getConnectedPanels();

    @Query(value = "SELECT mp FROM MainPanel AS mp WHERE mp.id NOT IN :connectedPanelIds AND mp.isActive=TRUE ")
    List<MainPanel> getUnconnectedPanels(List<Integer> connectedPanelIds);
}
