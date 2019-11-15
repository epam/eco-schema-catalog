/*
 * Copyright 2019 EPAM Systems
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.epam.eco.schemacatalog.fts;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * @author Andrei_Tytsik
 */
public final class SearchParams extends AbstractPagedQuery {

    private String query;

    private List<String> subjectTerm;
    private List<String> nameTerm;
    private List<String> fullnameTerm;
    private List<Integer> deletedTerm;
    private List<Integer> versionTerm;
    private List<Integer> versionLatestTerm;
    private List<String> compatibilityTerm;
    private List<String> modeTerm;
    private List<String> namespaceTerm;
    private List<String> metadataUpdatedByTerm;

    private String subjectRegExp;
    private String nameRegExp;
    private String fullnameRegExp;
    private String namespaceRegExp;
    private String metadataUpdatedByRegExp;

    private Float schemaRegistryIdBoost;
    private Float subjectBoost;
    private Float versionBoost;
    private Float versionLatestBoost;
    private Float compatibilityBoost;
    private Float modeBoost;
    private Float nameBoost;
    private Float namespaceBoost;
    private Float fullnameBoost;
    private Float deletedBoost;
    private Float metadataDocBoost;
    private Float metadataAttributeKeyBoost;
    private Float metadataAttributeValueBoost;
    private Float metadataUpdatedByBoost;
    private Float propertyKeysBoost;
    private Float propertyValuesBoost;

    private AggregationParams versionAggregation;
    private AggregationParams versionLatestAggregation;
    private AggregationParams deletedAggregation;
    private AggregationParams compatibilityAggregation;
    private AggregationParams modeAggregation;
    private AggregationParams namespaceAggregation;
    private AggregationParams metadataUpdatedByAggregation;

    public SearchParams() {
        this(true);
    }

    public SearchParams(boolean setAggregationsDefaults) {
        if (setAggregationsDefaults) {
            setAggregationsDefaults();
        }
    }

    public String getQuery() {
        return query;
    }
    public void setQuery(String query) {
        this.query = query;
    }
    public List<String> getSubjectTerm() {
        return subjectTerm;
    }
    public void setSubjectTerm(List<String> subjectTerm) {
        this.subjectTerm = subjectTerm;
    }
    public List<String> getNameTerm() {
        return nameTerm;
    }
    public void setNameTerm(List<String> nameTerm) {
        this.nameTerm = nameTerm;
    }
    public List<String> getFullnameTerm() {
        return fullnameTerm;
    }
    public void setFullnameTerm(List<String> fullnameTerm) {
        this.fullnameTerm = fullnameTerm;
    }
    public List<Integer> getVersionTerm() {
        return versionTerm;
    }
    public void setVersionTerm(List<Integer> versionTerm) {
        this.versionTerm = versionTerm;
    }
    public List<Integer> getVersionLatestTerm() {
        return versionLatestTerm;
    }
    public void setVersionLatestTerm(List<Integer> versionLatestTerm) {
        this.versionLatestTerm = versionLatestTerm;
    }
    public List<String> getCompatibilityTerm() {
        return compatibilityTerm;
    }
    public void setCompatibilityTerm(List<String> compatibilityTerm) {
        this.compatibilityTerm = compatibilityTerm;
    }
    public List<String> getModeTerm() {
        return modeTerm;
    }
    public void setModeTerm(List<String> modeTerm) {
        this.modeTerm = modeTerm;
    }
    public List<String> getNamespaceTerm() {
        return namespaceTerm;
    }
    public void setNamespaceTerm(List<String> namespaceTerm) {
        this.namespaceTerm = namespaceTerm;
    }
    public List<String> getMetadataUpdatedByTerm() {
        return metadataUpdatedByTerm;
    }
    public void setMetadataUpdatedByTerm(List<String> metadataUpdatedByTerm) {
        this.metadataUpdatedByTerm = metadataUpdatedByTerm;
    }
    public List<Integer> getDeletedTerm() {
        return deletedTerm;
    }
    public void setDeletedTerm(List<Integer> deletedTerm) {
        this.deletedTerm = deletedTerm;
    }
    public String getSubjectRegExp() {
        return subjectRegExp;
    }
    public void setSubjectRegExp(String subjectRegExp) {
        this.subjectRegExp = subjectRegExp;
    }
    public String getNameRegExp() {
        return nameRegExp;
    }
    public void setNameRegExp(String nameRegExp) {
        this.nameRegExp = nameRegExp;
    }
    public String getFullnameRegExp() {
        return fullnameRegExp;
    }
    public void setFullnameRegExp(String fullnameRegExp) {
        this.fullnameRegExp = fullnameRegExp;
    }
    public String getNamespaceRegExp() {
        return namespaceRegExp;
    }
    public void setNamespaceRegExp(String namespaceRegExp) {
        this.namespaceRegExp = namespaceRegExp;
    }
    public String getMetadataUpdatedByRegExp() {
        return metadataUpdatedByRegExp;
    }
    public void setMetadataUpdatedByRegExp(String metadataUpdatedByRegExp) {
        this.metadataUpdatedByRegExp = metadataUpdatedByRegExp;
    }
    public Float getSchemaRegistryIdBoost() {
        return schemaRegistryIdBoost;
    }
    public void setSchemaRegistryIdBoost(Float schemaRegistryIdBoost) {
        this.schemaRegistryIdBoost = schemaRegistryIdBoost;
    }
    public Float getSubjectBoost() {
        return subjectBoost;
    }
    public void setSubjectBoost(Float subjectBoost) {
        this.subjectBoost = subjectBoost;
    }
    public Float getVersionBoost() {
        return versionBoost;
    }
    public void setVersionBoost(Float versionBoost) {
        this.versionBoost = versionBoost;
    }
    public Float getVersionLatestBoost() {
        return versionLatestBoost;
    }
    public void setVersionLatestBoost(Float versionLatestBoost) {
        this.versionLatestBoost = versionLatestBoost;
    }
    public Float getCompatibilityBoost() {
        return compatibilityBoost;
    }
    public void setCompatibilityBoost(Float compatibilityBoost) {
        this.compatibilityBoost = compatibilityBoost;
    }
    public Float getModeBoost() {
        return modeBoost;
    }
    public void setModeBoost(Float modeBoost) {
        this.modeBoost = modeBoost;
    }
    public Float getNameBoost() {
        return nameBoost;
    }
    public void setNameBoost(Float nameBoost) {
        this.nameBoost = nameBoost;
    }
    public Float getNamespaceBoost() {
        return namespaceBoost;
    }
    public void setNamespaceBoost(Float namespaceBoost) {
        this.namespaceBoost = namespaceBoost;
    }
    public Float getFullnameBoost() {
        return fullnameBoost;
    }
    public Float getDeletedBoost() {
        return deletedBoost;
    }
    public void setDeletedBoost(Float deletedBoost) {
        this.deletedBoost = deletedBoost;
    }
    public Float getMetadataDocBoost() {
        return metadataDocBoost;
    }
    public void setMetadataDocBoost(Float metadataDocBoost) {
        this.metadataDocBoost = metadataDocBoost;
    }
    public Float getMetadataUpdatedByBoost() {
        return metadataUpdatedByBoost;
    }
    public void setMetadataUpdatedByBoost(Float metadataUpdatedByBoost) {
        this.metadataUpdatedByBoost = metadataUpdatedByBoost;
    }
    public Float getMetadataAttributeKeyBoost() {
        return metadataAttributeKeyBoost;
    }
    public void setMetadataAttributeKeyBoost(Float metadataAttributeKeyBoost) {
        this.metadataAttributeKeyBoost = metadataAttributeKeyBoost;
    }
    public Float getMetadataAttributeValueBoost() {
        return metadataAttributeValueBoost;
    }
    public void setMetadataAttributeValueBoost(Float metadataAttributeValueBoost) {
        this.metadataAttributeValueBoost = metadataAttributeValueBoost;
    }
    public void setFullnameBoost(Float fullnameBoost) {
        this.fullnameBoost = fullnameBoost;
    }
    public Float getPropertyKeysBoost() {
        return propertyKeysBoost;
    }
    public void setPropertyKeysBoost(Float propertyKeysBoost) {
        this.propertyKeysBoost = propertyKeysBoost;
    }
    public Float getPropertyValuesBoost() {
        return propertyValuesBoost;
    }
    public void setPropertyValuesBoost(Float propertyValuesBoost) {
        this.propertyValuesBoost = propertyValuesBoost;
    }
    public AggregationParams getVersionAggregation() {
        return versionAggregation;
    }
    public void setVersionAggregation(AggregationParams versionAggregation) {
        this.versionAggregation = versionAggregation;
    }
    public AggregationParams getVersionLatestAggregation() {
        return versionLatestAggregation;
    }
    public void setVersionLatestAggregation(AggregationParams versionLatestAggregation) {
        this.versionLatestAggregation = versionLatestAggregation;
    }
    public AggregationParams getCompatibilityAggregation() {
        return compatibilityAggregation;
    }
    public void setCompatibilityAggregation(AggregationParams compatibilityAggregation) {
        this.compatibilityAggregation = compatibilityAggregation;
    }
    public AggregationParams getModeAggregation() {
        return modeAggregation;
    }
    public void setModeAggregation(AggregationParams modeAggregation) {
        this.modeAggregation = modeAggregation;
    }
    public AggregationParams getNamespaceAggregation() {
        return namespaceAggregation;
    }
    public void setNamespaceAggregation(AggregationParams namespaceAggregation) {
        this.namespaceAggregation = namespaceAggregation;
    }
    public AggregationParams getMetadataUpdatedByAggregation() {
        return metadataUpdatedByAggregation;
    }
    public void setMetadataUpdatedByAggregation(AggregationParams metadataUpdatedByAggregation) {
        this.metadataUpdatedByAggregation = metadataUpdatedByAggregation;
    }
    public AggregationParams getDeletedAggregation() {
        return deletedAggregation;
    }
    public void setDeletedAggregation(AggregationParams deletedAggregation) {
        this.deletedAggregation = deletedAggregation;
    }

    public List<AggregationParams> getAggregations() {
        List<AggregationParams> aggregations = new ArrayList<>();
        if (versionAggregation != null) {
            aggregations.add(versionAggregation);
        }
        if (versionLatestAggregation != null) {
            aggregations.add(versionLatestAggregation);
        }
        if (compatibilityAggregation != null) {
            aggregations.add(compatibilityAggregation);
        }
        if (modeAggregation != null) {
            aggregations.add(modeAggregation);
        }
        if (namespaceAggregation != null) {
            aggregations.add(namespaceAggregation);
        }
        if (metadataUpdatedByAggregation != null) {
            aggregations.add(metadataUpdatedByAggregation);
        }
        if (deletedAggregation != null) {
            aggregations.add(deletedAggregation);
        }
        return aggregations;
    }

    public final void setAggregationsDefaults() {
        versionAggregation = FtsConstants.defaultVersionAggregation();
        versionLatestAggregation = FtsConstants.defaultVersionLatestAggregation();
        deletedAggregation = FtsConstants.defaultDeletedAggregation();
        compatibilityAggregation = FtsConstants.defaultCompatibilityAggregation();
        modeAggregation = FtsConstants.defaultModeAggregation();
        namespaceAggregation = FtsConstants.defaultNamespaceAggregation();
        metadataUpdatedByAggregation = FtsConstants.defaultMetadataUpdatedByAggregation();
    }

    public void setAggregationsNulls() {
        versionAggregation = null;
        versionLatestAggregation = null;
        deletedAggregation = null;
        compatibilityAggregation = null;
        modeAggregation = null;
        namespaceAggregation = null;
        metadataUpdatedByAggregation = null;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        SearchParams that = (SearchParams) obj;
        return
                Objects.equals(this.query, that.query) &&

                Objects.equals(this.subjectTerm, that.subjectTerm) &&
                Objects.equals(this.nameTerm, that.nameTerm) &&
                Objects.equals(this.fullnameTerm, that.fullnameTerm) &&
                Objects.equals(this.deletedTerm, that.deletedTerm) &&
                Objects.equals(this.versionTerm, that.versionTerm) &&
                Objects.equals(this.versionLatestTerm, that.versionLatestTerm) &&
                Objects.equals(this.compatibilityTerm, that.compatibilityTerm) &&
                Objects.equals(this.modeTerm, that.modeTerm) &&
                Objects.equals(this.namespaceTerm, that.namespaceTerm) &&
                Objects.equals(this.metadataUpdatedByTerm, that.metadataUpdatedByTerm) &&

                Objects.equals(this.subjectRegExp, that.subjectRegExp) &&
                Objects.equals(this.nameRegExp, that.nameRegExp) &&
                Objects.equals(this.fullnameRegExp, that.fullnameRegExp) &&
                Objects.equals(this.namespaceRegExp, that.namespaceRegExp) &&
                Objects.equals(this.metadataUpdatedByRegExp, that.metadataUpdatedByRegExp) &&

                Objects.equals(this.schemaRegistryIdBoost, that.schemaRegistryIdBoost) &&
                Objects.equals(this.subjectBoost, that.subjectBoost) &&
                Objects.equals(this.versionBoost, that.versionBoost) &&
                Objects.equals(this.versionLatestBoost, that.versionLatestBoost) &&
                Objects.equals(this.compatibilityBoost, that.compatibilityBoost) &&
                Objects.equals(this.modeBoost, that.modeBoost) &&
                Objects.equals(this.nameBoost, that.nameBoost) &&
                Objects.equals(this.namespaceBoost, that.namespaceBoost) &&
                Objects.equals(this.fullnameBoost, that.fullnameBoost) &&
                Objects.equals(this.deletedBoost, that.deletedBoost) &&
                Objects.equals(this.metadataDocBoost, that.metadataDocBoost) &&
                Objects.equals(this.metadataAttributeKeyBoost, that.metadataAttributeKeyBoost) &&
                Objects.equals(this.metadataAttributeValueBoost, that.metadataAttributeValueBoost) &&
                Objects.equals(this.metadataUpdatedByBoost, that.metadataUpdatedByBoost) &&
                Objects.equals(this.propertyKeysBoost, that.propertyKeysBoost) &&
                Objects.equals(this.propertyValuesBoost, that.propertyValuesBoost) &&

                Objects.equals(this.versionAggregation, that.versionAggregation) &&
                Objects.equals(this.versionLatestAggregation, that.versionLatestAggregation) &&
                Objects.equals(this.deletedAggregation, that.deletedAggregation) &&
                Objects.equals(this.compatibilityAggregation, that.compatibilityAggregation) &&
                Objects.equals(this.modeAggregation, that.modeAggregation) &&
                Objects.equals(this.namespaceAggregation, that.namespaceAggregation) &&
                Objects.equals(this.metadataUpdatedByAggregation, that.metadataUpdatedByAggregation) &&

                Objects.equals(this.getPage(), that.getPage()) &&
                Objects.equals(this.getPageSize(), that.getPageSize());
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                query,

                subjectTerm,
                nameTerm,
                fullnameTerm,
                deletedTerm,
                versionTerm,
                versionLatestTerm,
                compatibilityTerm,
                modeTerm,
                namespaceTerm,
                metadataUpdatedByTerm,

                subjectRegExp,
                nameRegExp,
                fullnameRegExp,
                namespaceRegExp,
                metadataUpdatedByRegExp,

                schemaRegistryIdBoost,
                subjectBoost,
                versionBoost,
                versionLatestBoost,
                compatibilityBoost,
                modeBoost,
                nameBoost,
                namespaceBoost,
                fullnameBoost,
                deletedBoost,
                metadataDocBoost,
                metadataAttributeKeyBoost,
                metadataAttributeValueBoost,
                metadataUpdatedByBoost,
                propertyKeysBoost,
                propertyValuesBoost,

                versionAggregation,
                versionLatestAggregation,
                deletedAggregation,
                compatibilityAggregation,
                modeAggregation,
                namespaceAggregation,
                metadataUpdatedByAggregation,

                getPage(),
                getPageSize());
    }

    @Override
    public String toString() {
        return
                "{query: " + query +
                ", subjectTerm: " + subjectTerm +
                ", nameTerm: " + nameTerm +
                ", fullnameTerm: " + fullnameTerm +
                ", deletedTerm: " + deletedTerm +
                ", versionTerm: " + versionTerm +
                ", versionLatestTerm: " + versionLatestTerm +
                ", compatibilityTerm: " + compatibilityTerm +
                ", modeTerm: " + modeTerm +
                ", namespaceTerm: " + namespaceTerm +
                ", metadataUpdatedByTerm: " + metadataUpdatedByTerm +

                ", subjectRegExp: " + subjectRegExp +
                ", nameRegExp: " + nameRegExp +
                ", fullnameRegExp: " + fullnameRegExp +
                ", namespaceRegExp: " + namespaceRegExp +
                ", metadataUpdatedByRegExp: " + metadataUpdatedByRegExp +

                ", schemaRegistryIdBoost: " + schemaRegistryIdBoost +
                ", subjectBoost: " + subjectBoost +
                ", versionBoost: " + versionBoost +
                ", versionLatestBoost: " + versionLatestBoost +
                ", compatibilityBoost: " + compatibilityBoost +
                ", modeBoost: " + modeBoost +
                ", nameBoost: " + nameBoost +
                ", namespaceBoost: " + namespaceBoost +
                ", fullnameBoost: " + fullnameBoost +
                ", deletedBoost: " + deletedBoost +
                ", metadataDocBoost: " + metadataDocBoost +
                ", metadataAttributeKeyBoost: " + metadataAttributeKeyBoost +
                ", metadataAttributeValueBoost: " + metadataAttributeValueBoost +
                ", metadataUpdatedByBoost: " + metadataUpdatedByBoost +
                ", propertyKeysBoost: " + propertyKeysBoost +
                ", propertyValuesBoost: " + propertyValuesBoost +

                ", versionAggregation: " + versionAggregation +
                ", versionLatestAggregation: " + versionLatestAggregation +
                ", deletedAggregation: " + deletedAggregation +
                ", compatibilityAggregation: " + compatibilityAggregation +
                ", modeAggregation: " + modeAggregation +
                ", namespaceAggregation: " + namespaceAggregation +
                ", metadataUpdatedByAggregation: " + metadataUpdatedByAggregation +

                ", page: " + getPage() +
                ", pageSize: " + getPageSize() +
                "}";
    }

}
