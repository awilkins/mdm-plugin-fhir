///*
// * Copyright 2020-2022 University of Oxford and Health and Social Care Information Centre, also known as NHS Digital
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// *
// * SPDX-License-Identifier: Apache-2.0
// */
//package uk.ac.ox.softeng.maurodatamapper.plugins.fhir.terminology.provider.importer
//
//import uk.ac.ox.softeng.maurodatamapper.api.exception.ApiBadRequestException
//import uk.ac.ox.softeng.maurodatamapper.api.exception.ApiUnauthorizedException
//import uk.ac.ox.softeng.maurodatamapper.core.authority.AuthorityService
//import uk.ac.ox.softeng.maurodatamapper.core.traits.provider.importer.JsonImportMapping
//import uk.ac.ox.softeng.maurodatamapper.plugins.fhir.ImportDataHandling
//import uk.ac.ox.softeng.maurodatamapper.plugins.fhir.MetadataHandling
//import uk.ac.ox.softeng.maurodatamapper.plugins.fhir.terminology.provider.exporter.FhirTerminologyExporterProviderService
//import uk.ac.ox.softeng.maurodatamapper.plugins.fhir.terminology.provider.importer.parameter.FhirTerminologyImporterProviderServiceParameters
//import uk.ac.ox.softeng.maurodatamapper.plugins.fhir.web.client.FhirServerClient
//import uk.ac.ox.softeng.maurodatamapper.security.User
//import uk.ac.ox.softeng.maurodatamapper.terminology.Terminology
//import uk.ac.ox.softeng.maurodatamapper.terminology.TerminologyService
//import uk.ac.ox.softeng.maurodatamapper.terminology.item.Term
//import uk.ac.ox.softeng.maurodatamapper.terminology.provider.importer.TerminologyImporterProviderService
//import uk.ac.ox.softeng.maurodatamapper.terminology.provider.importer.parameter.TerminologyFileImporterProviderServiceParameters
//
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.context.ApplicationContext
//
//class FhirFileTerminologyImporterProviderService extends TerminologyImporterProviderService<TerminologyFileImporterProviderServiceParameters>
//    implements MetadataHandling, ImportDataHandling<Terminology, TerminologyFileImporterProviderServiceParameters>, JsonImportMapping {
//
//    private static List<String> TERMINOLOGY_NON_METADATA_KEYS = ['id', 'name', 'description', 'publisher', 'concept']
//    private static List<String> TERM_NON_METADATA_KEYS = ['code', 'definition', 'display']
//
//    TerminologyService terminologyService
//    AuthorityService authorityService
//
//    @Autowired
//    ApplicationContext applicationContext
//
//    @Override
//    String getDisplayName() {
//        'FHIR File Terminology Importer'
//    }
//
//    @Override
//    String getVersion() {
//        getClass().getPackage().getSpecificationVersion() ?: 'SNAPSHOT'
//    }
//
//    @Override
//    String getNamespace() {
//        'uk.ac.ox.softeng.maurodatamapper.plugins.fhir.terminology.file'
//    }
//
//    @Override
//    Boolean handlesContentType(String contentType) {
//        false //contentType.equalsIgnoreCase(FhirTerminologyExporterProviderService.CONTENT_TYPE)
//    }
//
//    @Override
//    Boolean allowsExtraMetadataKeys() {
//        true
//    }
//
//    @Override
//    Boolean canImportMultipleDomains() {
//        false
//    }
//
//    @Override
//    Terminology updateImportedModelFromParameters(Terminology importedModel, TerminologyFileImporterProviderServiceParameters params, boolean list) {
//        updateFhirImportedModelFromParameters(importedModel, params, list)
//    }
//
//    @Override
//    Terminology checkImport(User currentUser, Terminology importedModel, TerminologyFileImporterProviderServiceParameters params) {
//        checkFhirImport(currentUser, importedModel, params)
//    }
//
//    @Override
//    Terminology importModel(User user, TerminologyFileImporterProviderServiceParameters params) {
//        if (!user) throw new ApiUnauthorizedException('FHIR01', 'User must be logged in to import model')
//        //if (!params.modelName) throw new ApiBadRequestException('FHIR02', 'Cannot import a single Terminology without the Terminology name')
//        log.debug('Import Terminology {}', params.modelName)
//        //FhirServerClient fhirServerClient = new FhirServerClient(params.fhirHost, params.fhirVersion, applicationContext)
//        importTerminology(user, params.modelName, params.importFile.fileContents)
//    }
//
//    @Override
//    List<Terminology> importModels(User user, TerminologyFileImporterProviderServiceParameters params) {
//        /*if (!user) throw new ApiUnauthorizedException('FHIR01', 'User must be logged in to import model')
//
//        if (params.modelName) {
//            log.debug('Model name supplied, only importing 1 model')
//            return [importModel(user, params)]
//        }
//
//        log.debug('Import Terminologies version {}', params.fhirVersion ?: 'Current')
//        FhirServerClient fhirServerClient = new FhirServerClient(params.fhirHost, params.fhirVersion, applicationContext)
//        // Just get the first entry as this will tell us how many there are
//        Map<String, Object> countResponse = fhirServerClient.getCodeSystemCount()
//
//        // Now get the full list
//        Map<String, Object> valueSets = fhirServerClient.getCodeSystems(countResponse.total as int)
//
//        // Collect all the entries as datamodels
//        valueSets.entry.collect {Map entry ->
//            importTerminology(fhirServerClient, user, params.fhirVersion, entry.resource.id)
//        }*/
//    }
//
//    Terminology importTerminology(User currentUser, String terminologyName, byte[] content) {
//
//        log.debug('Importing Terminology {} from FHIR version {}', terminologyName, version ?: 'Current')
//
//        // Load the map for that datamodel name
//        Map<String, Object> data = slurpAndClean(content, [])
//
//        Terminology terminology = new Terminology(label: data.id, description: data.description, organisation: data.publisher, aliases: [data.name],
//                                                  authority: authorityService.getDefaultAuthority())
//        processMetadata(data, terminology, namespace, TERMINOLOGY_NON_METADATA_KEYS)
//
//        data.concept.each {Map concept ->
//            Term term = new Term(code: concept.code).tap {
//                if (concept.definition) {
//                    it.definition = concept.definition
//                    it.description = concept.display
//                } else {
//                    it.definition = concept.display
//                }
//            }
//            processMetadata(concept, term, namespace, TERM_NON_METADATA_KEYS)
//            terminology.addToTerms(term)
//        }
//
//        terminologyService.checkImportedTerminologyAssociations(currentUser, terminology)
//        terminology
//    }
//}
