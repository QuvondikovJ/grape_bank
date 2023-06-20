package com.example.uzum.controller;

import com.example.uzum.dto.mainPanel.MainPanelDTO;
import com.example.uzum.dto.Result;
import com.example.uzum.service.MainPanelService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/main-panel")
public class MainPanelController {


    @Autowired
    private MainPanelService mainPanelService;

    @ApiOperation(value = " Thi method is used to add new panel.")
    @PreAuthorize(value = "hasAnyAuthority('ROLE_DIRECTOR', 'ROLE_ADMIN', 'MAIN_PANEL_ADD')")
    @PostMapping("/add")
    public Result<?> add(@Valid @RequestBody MainPanelDTO mainPanelDto) {
        return mainPanelService.add(mainPanelDto);
    }

    @ApiOperation(value = "This method is used to get all of panels.")
    @GetMapping("/getAll")
    public Result<?> getAll() {
        return mainPanelService.getAll();
    }

    @ApiOperation(value = "This method is used to get panel by its ID.")
    @GetMapping("/getById/{id}")
    public Result<?> getById(@PathVariable Integer id) {
        return mainPanelService.getById(id);
    }

    @ApiOperation(value = "This method is used to get orders count.")
    @GetMapping("/getOrdersCount")
    public Result<?> getOrdersCount() {
        return mainPanelService.getOrdersCount();
    }

    @ApiOperation(value = "This method is used to edit panel details by its ID.")
    @PreAuthorize(value = "hasAnyAuthority('ROLE_DIRECTOR', 'ROLE_ADMIN', 'MAIN_PANEL_EDIT')")
    @PutMapping("/edit/{id}")
    public Result<?> edit(@PathVariable Integer id, @Valid @RequestBody MainPanelDTO mainPanelDto) {
        return mainPanelService.edit(id, mainPanelDto);
    }

    @ApiOperation(value = "This method is used to deactivate panel.")
    @PreAuthorize(value = "hasAnyAuthority('ROLE_DIRECTOR', 'ROLE_ADMIN', 'MAIN_PANEL_DELETE')")
    @DeleteMapping("/delete/{id}")
    public Result<?> delete(@PathVariable Integer id) {
        return mainPanelService.delete(id);
    }

    @ApiOperation(value = "This method is used to connect panels to methods.")
    @PreAuthorize(value = "hasAnyAuthority('ROLE_DIRECTOR', 'ROLE_ADMIN', 'MAIN_PANEL_CONNECT_TO_METHOD')")
    @PostMapping("/connectPanelsToMethods")
    public Result<?> connectPanelsToMethods(@RequestParam String panelId,
                                            @RequestParam String methodName) {
        return mainPanelService.connectPanelsToMethods(panelId, methodName);
    }


    @ApiOperation(value = "This method is used to connect panels to sellers.")
    @PreAuthorize(value = "hasAnyAuthority('ROLE_DIRECTOR', 'ROLE_ADMIN', 'MAIN_PANEL_CONNECT_TO_SELLER')")
    @PostMapping("/connectPanelsToSellers")
    public Result<?> connectPanelsToSellers(@RequestParam String panelId,
                                            @RequestParam String sellerId) {
        return mainPanelService.connectPanelsToSellers(panelId, sellerId);
    }


    @ApiOperation(value = "This method is used to connect panels to categories.")
    @PreAuthorize(value = "hasAnyAuthority('ROLE_DIRECTOR', 'ROLE_ADMIN', 'MAIN_PANEL_CONNECT_TO_CATEGORY')")
    @PostMapping("/connectPanelsToCategories")
    public Result<?> connectPanelsToCategories(@RequestParam String panelId,
                                            @RequestParam String categoryId) {
        return mainPanelService.connectPanelsToCategories(panelId, categoryId);
    }

    @ApiOperation(value = "This method is used to connect panels to products.")
    @PreAuthorize(value = "hasAnyAuthority('ROLE_DIRECTOR', 'ROLE_ADMIN', 'MAIN_PANEL_CONNECT_TO_PRODUCT')")
    @PostMapping("/connectPanelsToProducts")
    public Result<?> connectPanelsToProducts(@RequestParam String panelId,
                                            @RequestParam String productId) {
        return mainPanelService.connectPanelsToProducts(panelId, productId);
    }

    @ApiOperation(value = "This method is used to get unconnected method names to connect to panel.")
    @PreAuthorize(value = "hasAnyAuthority('ROLE_DIRECTOR', 'ROLE_ADMIN', 'MAIN_PANEL_GET_METHOD_NAMES')")
    @GetMapping("/getMethodNamesToAddPanel")
    public Result<?> getMethodNamesToAddPanel() {
        return mainPanelService.getMethodNamesToAddPanel();
    }

    @ApiOperation(value = "This method is used to get unconnected panels to connect to method.")
    @PreAuthorize(value = "hasAnyAuthority('ROLE_DIRECTOR', 'ROLE_ADMIN', 'MAIN_PANEL_GET_UNCONNECTED_PANELS')")
    @GetMapping("/getUnconnectedPanels")
    public Result<?> getUnconnectedPanels() {
        return mainPanelService.getUnconnectedPanels();
    }

    @ApiOperation(value = "This method is used to get connected panels.")
    @PreAuthorize(value = "hasAnyAuthority('ROLE_DIRECTOR', 'ROLE_ADMIN', 'MAIN_PANEL_GET_CONNECTED_PANELS')")
    @GetMapping("/getConnectedPanels")
    public Result<?> getConnectedPanels(){
        return mainPanelService.getConnectedPanels();
    }

    @ApiOperation(value = "This method is used to disconnect panels from anything.")
    @PreAuthorize(value = "hasAnyAuthority('ROLE_DIRECTOR', 'ROLE_ADMIN', 'MAIN_PANEL_DISCONNECT_FROM_ANYTHING')")
    @DeleteMapping("/disconnectPanel/{id}")
    public Result<?> disconnectPanel(@PathVariable Integer id) {
        return mainPanelService.disconnectPanel(id);
    }

}
