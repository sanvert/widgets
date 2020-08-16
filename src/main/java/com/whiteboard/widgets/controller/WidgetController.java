package com.whiteboard.widgets.controller;

import com.whiteboard.widgets.model.PageInfo;
import com.whiteboard.widgets.model.Widget;
import com.whiteboard.widgets.service.WidgetService;
import com.whiteboard.widgets.util.Operation;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/widgets")
@RequiredArgsConstructor
public class WidgetController {

    private final WidgetService widgetService;

    @GetMapping("/{id}")
    public ResponseEntity<Widget> getWidget(@PathVariable String id, HttpServletRequest request) {
        Widget widget = widgetService.apply(Operation.GET, id, request);
        return widget == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(widget);
    }

    @GetMapping
    public List<Widget> getAllWidgetsSorted(@Valid PageInfo pageInfo, HttpServletRequest request) {
        return widgetService.apply(Operation.GET_ALL_SORTED, pageInfo, request);
    }

    @PostMapping
    public ResponseEntity<Widget> createWidget(@RequestBody @Valid Widget widget, HttpServletRequest request) {
        Widget created = widgetService.apply(Operation.CREATE, widget, request);
        if (created == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(created);
    }

    @PutMapping
    public ResponseEntity<Widget> updateWidget(@RequestBody @Valid Widget widget, HttpServletRequest request) {
        Widget created = widgetService.apply(Operation.UPDATE, widget, request);
        if (created == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(created);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWidget(@PathVariable String id, HttpServletRequest request) {
        boolean deleted = widgetService.apply(Operation.DELETE, id, request);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
