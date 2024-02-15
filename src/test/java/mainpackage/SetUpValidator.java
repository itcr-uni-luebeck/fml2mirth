package mainpackage;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.support.DefaultProfileValidationSupport;
import ca.uhn.fhir.validation.FhirValidator;
import org.hl7.fhir.common.hapi.validation.support.*;
import org.hl7.fhir.common.hapi.validation.validator.FhirInstanceValidator;

import java.io.IOException;

public class SetUpValidator {

    public static FhirValidator getValidator() {

        FhirContext ctx = FhirContext.forR4();

        NpmPackageValidationSupport npmPackageSupport = new NpmPackageValidationSupport(ctx);
        NpmPackageValidationSupport npmPackageSupport2 = new NpmPackageValidationSupport(ctx);
        NpmPackageValidationSupport npmPackageSupport3 = new NpmPackageValidationSupport(ctx);
        NpmPackageValidationSupport npmPackageSupport4 = new NpmPackageValidationSupport(ctx);
        // Mio-Laborbefund-Packages
//        try {
//            npmPackageSupport.loadPackageFromClasspath("classpath:/de.basisprofil.r4-0.9.13.tgz");
//            npmPackageSupport2.loadPackageFromClasspath("classpath:/hl7.fhir.r4.core-4.0.1.tgz");
//            npmPackageSupport3.loadPackageFromClasspath("classpath:/kbv.basis-1.2.1.tgz");
//            // edited in Profile for Diagnostic-Report: Laboratory report (record artifact) -> Laboratory report to match SNOMED-Validation
//            npmPackageSupport4.loadPackageFromClasspath("classpath:/kbv.mio.laborbefund-1.0.0-kommentierung.tgz");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        // EU-Laborbefund-Packages:
        try {
            npmPackageSupport2.loadPackageFromClasspath("classpath:/hl7.fhir.r4.core-4.0.1.tgz");
//          npmPackageSupport2.loadPackageFromClasspath("classpath:/hl7.fhir.r5.core-5.0.0.tar.gz");

            npmPackageSupport.loadPackageFromClasspath("classpath:/hl7.terminology.r4-5.3.0.tar.gz");
            npmPackageSupport2.loadPackageFromClasspath("classpath:/hl7.fhir.uv.ips-1.1.0.tar.gz");
            npmPackageSupport3.loadPackageFromClasspath("classpath:/hl7.fhir.uv.extensions.r4-1.0.0.tar.gz");
            npmPackageSupport4.loadPackageFromClasspath("classpath:/fhir.dicom-2022.4.20221006.tar.gz");
            npmPackageSupport4.loadPackageFromClasspath("classpath:/euLabBefProfiles.tar.gz");
        } catch (IOException e) {
            e.printStackTrace();
        }

        ValidationSupportChain validationSupportChain = new ValidationSupportChain(
                npmPackageSupport,
                npmPackageSupport2,
                npmPackageSupport3,
                npmPackageSupport4,
                new CommonCodeSystemsTerminologyService(ctx),
                new InMemoryTerminologyServerValidationSupport(ctx),
                new SnapshotGeneratingValidationSupport(ctx)
        );

           RemoteTerminologyServiceValidationSupport remoteValSupport = new RemoteTerminologyServiceValidationSupport(ctx);
           remoteValSupport.setBaseUrl("http://<IPofTerminologieServer:<port>/fhir");
           validationSupportChain.addValidationSupport(remoteValSupport);

        CachingValidationSupport validationSupport = new CachingValidationSupport(validationSupportChain);


        FhirValidator validator = ctx.newValidator();
        FhirInstanceValidator instanceValidator = new FhirInstanceValidator(validationSupport);
        validator.registerValidatorModule(instanceValidator);

        return validator;
    }


}
