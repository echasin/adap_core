package com.innvo.service;

import com.codahale.metrics.annotation.Timed;
import com.innvo.domain.*;
import com.innvo.repository.*;
import com.innvo.repository.search.*;
import org.elasticsearch.indices.IndexAlreadyExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.List;

@Service
public class ElasticsearchIndexService {

    private final Logger log = LoggerFactory.getLogger(ElasticsearchIndexService.class);

    @Inject
    private ActivityRepository activityRepository;

    @Inject
    private ActivitySearchRepository activitySearchRepository;

    @Inject
    private ActivitymbrRepository activitymbrRepository;

    @Inject
    private ActivitymbrSearchRepository activitymbrSearchRepository;

    @Inject
    private AssetRepository assetRepository;

    @Inject
    private AssetSearchRepository assetSearchRepository;

    @Inject
    private AssetassetmbrRepository assetassetmbrRepository;

    @Inject
    private AssetassetmbrSearchRepository assetassetmbrSearchRepository;

    @Inject
    private CategoryRepository categoryRepository;

    @Inject
    private CategorySearchRepository categorySearchRepository;

    @Inject
    private DashboardRepository dashboardRepository;

    @Inject
    private DashboardSearchRepository dashboardSearchRepository;

    @Inject
    private FiscalyearRepository fiscalyearRepository;

    @Inject
    private FiscalyearSearchRepository fiscalyearSearchRepository;

    @Inject
    private IdentifierRepository identifierRepository;

    @Inject
    private IdentifierSearchRepository identifierSearchRepository;

    @Inject
    private KeyRepository keyRepository;

    @Inject
    private KeySearchRepository keySearchRepository;

    @Inject
    private LocationRepository locationRepository;

    @Inject
    private LocationSearchRepository locationSearchRepository;

    @Inject
    private OrganizationRepository organizationRepository;

    @Inject
    private OrganizationSearchRepository organizationSearchRepository;

    @Inject
    private OrganizationorganizationmbrRepository organizationorganizationmbrRepository;

    @Inject
    private OrganizationorganizationmbrSearchRepository organizationorganizationmbrSearchRepository;

    @Inject
    private PortfolioRepository portfolioRepository;

    @Inject
    private PortfolioSearchRepository portfolioSearchRepository;

    @Inject
    private PortfolioprojectmbrRepository portfolioprojectmbrRepository;

    @Inject
    private PortfolioprojectmbrSearchRepository portfolioprojectmbrSearchRepository;

    @Inject
    private ProjectRepository projectRepository;

    @Inject
    private ProjectSearchRepository projectSearchRepository;

    @Inject
    private ProjectprojectmbrRepository projectprojectmbrRepository;

    @Inject
    private ProjectprojectmbrSearchRepository projectprojectmbrSearchRepository;

    @Inject
    private RecordtypeRepository recordtypeRepository;

    @Inject
    private RecordtypeSearchRepository recordtypeSearchRepository;

    @Inject
    private RequestRepository requestRepository;

    @Inject
    private RequestSearchRepository requestSearchRepository;

    @Inject
    private RequestprojectmbrRepository requestprojectmbrRepository;

    @Inject
    private RequestprojectmbrSearchRepository requestprojectmbrSearchRepository;

    @Inject
    private RequeststateRepository requeststateRepository;

    @Inject
    private RequeststateSearchRepository requeststateSearchRepository;

    @Inject
    private ScoreRepository scoreRepository;

    @Inject
    private ScoreSearchRepository scoreSearchRepository;

    @Inject
    private SecuritygroupRepository securitygroupRepository;

    @Inject
    private SecuritygroupSearchRepository securitygroupSearchRepository;

    @Inject
    private SecuritygroupruleRepository securitygroupruleRepository;

    @Inject
    private SecuritygroupruleSearchRepository securitygroupruleSearchRepository;

    @Inject
    private StrategyRepository strategyRepository;

    @Inject
    private StrategySearchRepository strategySearchRepository;

    @Inject
    private StrategymbrRepository strategymbrRepository;

    @Inject
    private StrategymbrSearchRepository strategymbrSearchRepository;

    @Inject
    private SubcategoryRepository subcategoryRepository;

    @Inject
    private SubcategorySearchRepository subcategorySearchRepository;

    @Inject
    private TagRepository tagRepository;

    @Inject
    private TagSearchRepository tagSearchRepository;

    @Inject
    private TagmbrRepository tagmbrRepository;

    @Inject
    private TagmbrSearchRepository tagmbrSearchRepository;

    @Inject
    private ElasticsearchTemplate elasticsearchTemplate;

    @Async
    @Timed
    public void reindexAll() {
        reindexForClass(Activity.class, activityRepository, activitySearchRepository);
        reindexForClass(Activitymbr.class, activitymbrRepository, activitymbrSearchRepository);
        reindexForClass(Asset.class, assetRepository, assetSearchRepository);
        reindexForClass(Assetassetmbr.class, assetassetmbrRepository, assetassetmbrSearchRepository);
        reindexForClass(Category.class, categoryRepository, categorySearchRepository);
        reindexForClass(Dashboard.class, dashboardRepository, dashboardSearchRepository);
        reindexForClass(Fiscalyear.class, fiscalyearRepository, fiscalyearSearchRepository);
        reindexForClass(Identifier.class, identifierRepository, identifierSearchRepository);
        reindexForClass(Key.class, keyRepository, keySearchRepository);
        reindexForClass(Location.class, locationRepository, locationSearchRepository);
        reindexForClass(Organization.class, organizationRepository, organizationSearchRepository);
        reindexForClass(Organizationorganizationmbr.class, organizationorganizationmbrRepository, organizationorganizationmbrSearchRepository);
        reindexForClass(Portfolio.class, portfolioRepository, portfolioSearchRepository);
        reindexForClass(Portfolioprojectmbr.class, portfolioprojectmbrRepository, portfolioprojectmbrSearchRepository);
        reindexForClass(Project.class, projectRepository, projectSearchRepository);
        reindexForClass(Projectprojectmbr.class, projectprojectmbrRepository, projectprojectmbrSearchRepository);
        reindexForClass(Recordtype.class, recordtypeRepository, recordtypeSearchRepository);
        reindexForClass(Request.class, requestRepository, requestSearchRepository);
        reindexForClass(Requestprojectmbr.class, requestprojectmbrRepository, requestprojectmbrSearchRepository);
        reindexForClass(Requeststate.class, requeststateRepository, requeststateSearchRepository);
        reindexForClass(Score.class, scoreRepository, scoreSearchRepository);
        reindexForClass(Securitygroup.class, securitygroupRepository, securitygroupSearchRepository);
        reindexForClass(Securitygrouprule.class, securitygroupruleRepository, securitygroupruleSearchRepository);
        reindexForClass(Strategy.class, strategyRepository, strategySearchRepository);
        reindexForClass(Strategymbr.class, strategymbrRepository, strategymbrSearchRepository);
        reindexForClass(Subcategory.class, subcategoryRepository, subcategorySearchRepository);
        reindexForClass(Tag.class, tagRepository, tagSearchRepository);
        reindexForClass(Tagmbr.class, tagmbrRepository, tagmbrSearchRepository);

        log.info("Elasticsearch: Successfully performed reindexing");
    }

    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    private <T, ID extends Serializable> void reindexForClass(Class<T> entityClass, JpaRepository<T, ID> jpaRepository,
                                                              ElasticsearchRepository<T, ID> elasticsearchRepository) {
        elasticsearchTemplate.deleteIndex(entityClass);
        try {
            elasticsearchTemplate.createIndex(entityClass);
        } catch (IndexAlreadyExistsException e) {
            // Do nothing. Index was already concurrently recreated by some other service.
        }
        elasticsearchTemplate.putMapping(entityClass);
        if (jpaRepository.count() > 0) {
            try {
                Method m = jpaRepository.getClass().getMethod("findAllWithEagerRelationships");
                elasticsearchRepository.save((List<T>) m.invoke(jpaRepository));
            } catch (Exception e) {
                elasticsearchRepository.save(jpaRepository.findAll());
            }
        }
        log.info("Elasticsearch: Indexed all rows for " + entityClass.getSimpleName());
    }
}
