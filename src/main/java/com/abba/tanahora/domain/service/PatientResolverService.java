package com.abba.tanahora.domain.service;

import com.abba.tanahora.domain.model.PatientRef;
import com.abba.tanahora.domain.model.User;

public interface PatientResolverService {

    PatientRef resolve(User user, String patientName, String lastPatientId, boolean createIfMissing);
}
