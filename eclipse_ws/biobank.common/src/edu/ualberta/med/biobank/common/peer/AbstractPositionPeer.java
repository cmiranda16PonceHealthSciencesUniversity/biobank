package edu.ualberta.med.biobank.common.peer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.ualberta.med.biobank.common.util.TypeReference;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.model.AbstractPosition;

public class AbstractPositionPeer {
    public static final Property<Integer, AbstractPosition> ID = Property
        .create("id" //$NON-NLS-1$
            , AbstractPosition.class
            , new TypeReference<Integer>() {
            }
            , new Property.Accessor<Integer, AbstractPosition>() {
                private static final long serialVersionUID = 1L;

                @Override
                public Integer get(AbstractPosition model) {
                    return model.getId();
                }

                @Override
                public void set(AbstractPosition model, Integer value) {
                    model.setId(value);
                }
            });

    public static final Property<Integer, AbstractPosition> COL = Property
        .create("col" //$NON-NLS-1$
            , AbstractPosition.class
            , new TypeReference<Integer>() {
            }
            , new Property.Accessor<Integer, AbstractPosition>() {
                private static final long serialVersionUID = 1L;

                @Override
                public Integer get(AbstractPosition model) {
                    return model.getCol();
                }

                @Override
                public void set(AbstractPosition model, Integer value) {
                    model.setCol(value);
                }
            });

    public static final Property<Integer, AbstractPosition> ROW = Property
        .create("row" //$NON-NLS-1$
            , AbstractPosition.class
            , new TypeReference<Integer>() {
            }
            , new Property.Accessor<Integer, AbstractPosition>() {
                private static final long serialVersionUID = 1L;

                @Override
                public Integer get(AbstractPosition model) {
                    return model.getRow();
                }

                @Override
                public void set(AbstractPosition model, Integer value) {
                    model.setRow(value);
                }
            });

    public static final List<Property<?, ? super AbstractPosition>> PROPERTIES;
    static {
        List<Property<?, ? super AbstractPosition>> aList =
            new ArrayList<Property<?, ? super AbstractPosition>>();
        aList.add(ID);
        aList.add(COL);
        aList.add(ROW);
        PROPERTIES = Collections.unmodifiableList(aList);
    };
}
