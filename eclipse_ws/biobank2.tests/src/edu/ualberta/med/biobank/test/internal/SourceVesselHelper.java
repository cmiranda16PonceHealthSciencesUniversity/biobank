package edu.ualberta.med.biobank.test.internal;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.SourceVesselWrapper;

public class SourceVesselHelper extends DbHelper {

    public static List<SourceVesselWrapper> createdSourceVessels = new ArrayList<SourceVesselWrapper>();

    public static SourceVesselWrapper newSourceVessel(String name,
        PatientWrapper patient, Date timeDrawn, Double volume) {
        SourceVesselWrapper source = new SourceVesselWrapper(appService);
        source.setName(name);
        source.setPatient(patient);
        source.setTimeDrawn(timeDrawn);
        source.setVolume(volume);
        return source;
    }

    public static SourceVesselWrapper addSourceVessel(String name,
        PatientWrapper patient, Date timeDrawn, Double volume,
        boolean addToCreatedList) throws Exception {
        SourceVesselWrapper source = newSourceVessel(name, patient, timeDrawn,
            volume);
        source.persist();
        if (addToCreatedList) {
            createdSourceVessels.add(source);
        }
        return source;
    }

    public static SourceVesselWrapper addSourceVessel(String name,
        PatientWrapper patient, Date timeDrawn, Double volume) throws Exception {
        return addSourceVessel(name, patient, timeDrawn, volume, true);
    }

    public static void deleteCreatedSourceVessels() throws Exception {
        for (SourceVesselWrapper source : createdSourceVessels) {
            source.reload();
            source.delete();
        }
        createdSourceVessels.clear();
    }

}
