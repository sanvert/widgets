package com.whiteboard.widgets.storage;

import com.whiteboard.widgets.model.PageInfo;
import com.whiteboard.widgets.model.Widget;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.SortedMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Repository
public class WidgetStore {

    private Map<String, Widget> widgets;
    private NavigableMap<Integer, Widget> widgetsSortedByZ;

    public WidgetStore() {
        this.widgets = new ConcurrentHashMap<>();
        this.widgetsSortedByZ = new ConcurrentSkipListMap<>();
    }

    public Widget get(String id) {
        Widget selected = widgets.get(id);
        if (selected == null) {
            return null;
        }
        synchronized (selected) {
            return selected;
        }
    }

    public List<Widget> getAll(PageInfo pageInfo) {
        return widgetsSortedByZ.values().stream()
            .skip((pageInfo.getPage() - 1) * pageInfo.getSize())
            .limit(pageInfo.getSize())
            .collect(Collectors.toList());
    }

    public Widget create(Widget widget) {
        widget.setId(UUID.randomUUID().toString());
        widget.setLastModificationDate(LocalDateTime.now());
        widgets.put(widget.getId(), widget);
        positionWidgetAndUpdateZ(widget, widget.getZ());
        return widget;
    }

    public Widget update(Widget widget, Widget current) {
        synchronized (current) {
            updateValues(widget, current);
            if (widget.getZ() != null && !widget.getZ().equals(current.getZ())) {
                positionWidgetAndUpdateZ(current, widget.getZ());
            }
        }
        return current;
    }

    private static void updateValues(Widget widget, Widget current) {
        current.setLastModificationDate(LocalDateTime.now());
        current.setX(widget.getX());
        current.setY(widget.getY());
        current.setWidth(widget.getWidth());
        current.setHeight(widget.getHeight());
    }

    private synchronized void positionWidgetAndUpdateZ(Widget widget, Integer newZ) {
        if (newZ == null) {
            Integer newKey = widgetsSortedByZ.isEmpty() ? 0 : widgetsSortedByZ.lastKey() + 1;
            widget.setZ(newKey);
        } else if (widgetsSortedByZ.containsKey(newZ) && !widget.equals(widgetsSortedByZ.get(newZ))) {
            shiftWidgetsHavingLargerKeys(newZ);
            widgetsSortedByZ.remove(widget.getZ());
            widget.setZ(newZ);
        }
        widgetsSortedByZ.put(widget.getZ(), widget);
    }

    private void shiftWidgetsHavingLargerKeys(Integer z) {
        SortedMap<Integer, Widget> tails = widgetsSortedByZ.tailMap(z, true);
        Map.Entry<Integer, Widget> previous = null;
        for (Map.Entry<Integer, Widget> current : tails.entrySet()) {
            if (previous != null && current.getKey() - previous.getKey() != 1) {
                break;
            }
            incrementZ(previous);
            previous = current;
        }
        incrementZ(previous);
    }

    private void incrementZ(Map.Entry<Integer, Widget> entry) {
        if (entry != null) {
            entry.getValue().setZ(entry.getKey() + 1);
            widgetsSortedByZ.put(entry.getValue().getZ(), entry.getValue());
        }
    }

    public Widget delete(String id) {
        Widget removed = widgets.remove(id);
        if (removed != null) {
            widgetsSortedByZ.remove(removed.getZ());
        }
        return removed;
    }
}
