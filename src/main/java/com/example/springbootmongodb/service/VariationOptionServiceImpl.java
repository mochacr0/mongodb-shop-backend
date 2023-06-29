package com.example.springbootmongodb.service;

import com.example.springbootmongodb.common.data.VariationOptionRequest;
import com.example.springbootmongodb.common.data.mapper.VariationOptionMapper;
import com.example.springbootmongodb.exception.UnprocessableContentException;
import com.example.springbootmongodb.model.ProductVariationEntity;
import com.example.springbootmongodb.model.VariationOptionEntity;
import com.example.springbootmongodb.repository.ProductVariationRepository;
import com.example.springbootmongodb.repository.VariationOptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class VariationOptionServiceImpl extends DataBaseService<VariationOptionEntity> implements VariationOptionService {
    private final VariationOptionRepository optionRepository;
    private final ProductVariationRepository variationRepository;
    private final VariationOptionMapper mapper;

    public static final String DUPLICATED_OPTION_NAME_ERROR_MESSAGE = "Cannot create options with same name";
    public static final String NON_EXISTENT_VARIATION_ERROR_MESSAGE = "Cannot refer to a non-existent variation";
    @Override
    public MongoRepository<VariationOptionEntity, String> getRepository() {
        return null;
    }
    @Override
    @Transactional
    public List<VariationOptionEntity> bulkCreate(List<VariationOptionRequest> requests, ProductVariationEntity variation) {
        log.info("Performing VariationOptionService bulkCreate");
        if (containsDuplicates(requests, VariationOptionRequest::getName)) {
            throw new UnprocessableContentException(DUPLICATED_OPTION_NAME_ERROR_MESSAGE);
        }
        if (!variationRepository.existsById(variation.getId())) {
            throw new UnprocessableContentException(NON_EXISTENT_VARIATION_ERROR_MESSAGE);
        }
        List<VariationOptionEntity> newOptions = new ArrayList<>();
        for (int i = 0; i < requests.size(); i++) {
            VariationOptionEntity newOption = mapper.toEntity(requests.get(i));
            newOption.setVariation(variation);
            newOption.setIndex(i);
            newOptions.add(newOption);
        }
        return optionRepository.bulkCreate(newOptions);
    }

}
