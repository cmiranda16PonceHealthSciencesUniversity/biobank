package edu.ualberta.med.biobank.model;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;

import org.eclipse.core.runtime.Assert;

import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class ModelUtils {

    public static List<Container> getTopContainersForSite(
        WritableApplicationService appService, Site site)
        throws ApplicationException {
        HQLCriteria criteria = new HQLCriteria("from "
            + Container.class.getName() + " where site.id = " + site.getId()
            + " and locatedAtPosition.parentContainer is null");
        return appService.query(criteria);
    }

    public static Container newContainer(Container parent) {
        Container newContainer = new Container();
        ContainerPosition position = new ContainerPosition();
        position.setParentContainer(parent);
        position.setContainer(newContainer);
        newContainer.setPosition(position);
        return newContainer;
    }

    public static Object getObjectWithId(WritableApplicationService appService,
        Class<?> classType, Integer id) throws Exception {
        Constructor<?> constructor = classType.getConstructor();
        Object instance = constructor.newInstance();
        Method setIdMethod = classType.getMethod("setId", Integer.class);
        setIdMethod.invoke(instance, id);

        List<?> list = appService.search(classType, instance);
        Assert.isTrue(list.size() == 1);
        return list.get(0);
    }

    public static ContainerType getCabinetType(
        WritableApplicationService appService) {
        ContainerType type = new ContainerType();
        type.setName("Cabinet");
        List<ContainerType> types;
        try {
            types = appService.search(ContainerType.class, type);
            if (types.size() == 1) {
                return types.get(0);
            }
        } catch (ApplicationException e) {
        }
        return null;
    }

    public static Container getContainerWithBarcode(
        WritableApplicationService appService, String barcode)
        throws ApplicationException {
        Container container = new Container();
        container.setBarcode(barcode);
        List<Container> containers = appService.search(Container.class,
            container);
        if (containers.size() == 1) {
            return containers.get(0);
        }
        return null;
    }

    public static String getSamplePosition(Sample sample) {
        SamplePosition position = sample.getSamplePosition();
        if (position == null) {
            return "none";
        } else {
            String positionString = getPositionString(position);
            Container container = position.getContainer();
            ContainerPosition containerPosition = container.getPosition();
            Container parent = containerPosition.getParentContainer();
            while (parent != null) {
                positionString = getPositionString(containerPosition) + ":"
                    + positionString;
                System.out.println(positionString);
                container = parent;
                containerPosition = parent.getPosition();
                parent = containerPosition.getParentContainer();
            }
            positionString = container.getBarcode() + ":" + positionString;
            return positionString;
        }
    }

    public static String getPositionString(AbstractPosition position) {
        int dim1 = position.getPositionDimensionOne();
        int dim2 = position.getPositionDimensionTwo();
        String dim1String = String.valueOf(dim1);
        String dim2String = String.valueOf(dim2);
        return dim1String + dim2String;
    }

    public static boolean getBooleanValue(Boolean value, boolean defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        return value.booleanValue();
    }
}
