package com.whiteboard.widgets.service;

import com.whiteboard.widgets.model.PageInfo;
import com.whiteboard.widgets.model.Widget;
import com.whiteboard.widgets.storage.WidgetStore;
import com.whiteboard.widgets.util.Constants;
import com.whiteboard.widgets.util.Operation;
import java.util.EnumMap;
import java.util.List;
import java.util.function.Function;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

@Service
public class WidgetService {

    public static final int DEFAULT_PAGE = 1;
    public static final int DEFAULT_SIZE = 10;

    private final WidgetStore widgetStore;
    private final EnumMap<Operation, Function> serviceFunctions;

    public WidgetService(WidgetStore widgetStore, RateLimiterService rateLimiterService) {
        this.widgetStore = widgetStore;

        this.serviceFunctions = new EnumMap<>(Operation.class);
        this.serviceFunctions.put(Operation.GET, rateLimiterService.generateDecorator(Operation.GET.name(), this::get));
        this.serviceFunctions.put(Operation.CREATE, rateLimiterService.generateDecorator(Operation.CREATE.name(), this::create));
        this.serviceFunctions.put(Operation.UPDATE, rateLimiterService.generateDecorator(Operation.UPDATE.name(), this::update));
        this.serviceFunctions.put(Operation.DELETE, rateLimiterService.generateDecorator(Operation.DELETE.name(), this::delete));
        this.serviceFunctions.put(Operation.GET_ALL_SORTED, rateLimiterService.generateDecorator(Operation.GET_ALL_SORTED.name(), this::getAllSorted));
    }

    private Widget get(String id) {
        return widgetStore.get(id);
    }

    private List<Widget> getAllSorted(PageInfo pageInfo) {
        if (pageInfo.getPage() == null) {
            pageInfo.setPage(DEFAULT_PAGE);
        }
        if (pageInfo.getSize() == null) {
            pageInfo.setSize(DEFAULT_SIZE);
        }
        return widgetStore.getAll(pageInfo);
    }

    private Widget create(Widget widget) {
        if (widget.getId() != null) {
            return null;
        }
        return widgetStore.create(widget);
    }

    private Widget update(Widget widget) {
        if (widget.getId() == null || widgetStore.get(widget.getId()) == null) {
            return null;
        }
        return widgetStore.update(widget, widgetStore.get(widget.getId()));
    }

    private boolean delete(String id) {
        return widgetStore.delete(id) != null;
    }

    public<I, O> O apply(Operation operation, I input, HttpServletRequest request) {
        request.setAttribute(Constants.OPERATION_KEYWORD, operation);
        return ((Function<I, O>)this.serviceFunctions.get(operation)).apply(input);
    }
}
