package com.example.uzum.repository;

import com.example.uzum.entity.MainPanel;
import com.example.uzum.entity.PanelConnectMethod;
import org.hibernate.validator.constraints.pl.REGON;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface PanelConnectMethodRepo extends JpaRepository<PanelConnectMethod, Integer> {


    @Query(value = "SELECT pcmethod.methodName FROM PanelConnectMethod AS pcmethod")
    List<String> getMethodNames();

    Optional<PanelConnectMethod> findByMainPanelId(Integer mainPanel_id);

    Optional<PanelConnectMethod> findByMethodName(String methodName);


    @Transactional
    @Modifying
    @Query(value = "DELETE FROM PanelConnectMethod AS pm WHERE pm.mainPanel.id=:panelId")
    void deleteByMainPanelId(Integer panelId);
}
