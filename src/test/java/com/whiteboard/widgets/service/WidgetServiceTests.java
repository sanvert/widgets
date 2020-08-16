package com.whiteboard.widgets.service;

import com.whiteboard.widgets.model.PageInfo;
import com.whiteboard.widgets.storage.WidgetStore;
import com.whiteboard.widgets.util.Operation;
import java.util.Collections;
import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

public class WidgetServiceTests {

    private WidgetStore widgetStore;
    private HttpServletRequest httpServletRequest;
    private WidgetService widgetService;

    @BeforeEach
    public void init() {
        widgetStore = Mockito.mock(WidgetStore.class);
        httpServletRequest = Mockito.mock(HttpServletRequest.class);

        widgetService = new WidgetService(widgetStore, new RateLimiterService());
    }

    @Test
    public void getAll_providedPageInfoValuesEmpty_pageInfoValuesAreSetDefault() {
        PageInfo pageInfo = new PageInfo();
        when(widgetStore.getAll(pageInfo)).thenReturn(Collections.emptyList());
        doNothing().when(httpServletRequest).setAttribute(anyString(), any());
        widgetService.apply(Operation.GET_ALL_SORTED, pageInfo, httpServletRequest);

        assertEquals(WidgetService.DEFAULT_PAGE, pageInfo.getPage());
        assertEquals(WidgetService.DEFAULT_SIZE, pageInfo.getSize());
    }
}
