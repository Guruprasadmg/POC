package com.poc;

import com.poc.model.ClientResponse;
import com.poc.repo.DataSetService;
import com.poc.service.UserDataSetServices;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;

import static org.mockito.Mockito.*;

class UserDataSetServicesTest {

    @InjectMocks
    private UserDataSetServices userDataSetServices;

    @Mock
    private DataSetService dataSetService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void bulkUpload_Success() throws IOException {
        String csvContent = "quarter,stock,date,open,high,low,close,volume,percent_change_price,percent_change_volume_over_last_wk,previous_weeks_volume,next_weeks_open,next_weeks_close,percent_change_next_weeks_price,days_to_next_dividend,percent_return_next_dividend\n" +
                "1,AA,1/14/2011,$16.71 ,$16.71 ,$15.64 ,$15.97 ,242963398,-4.42849,1.380223028,239655616,$16.19 ,$15.79 ,-2.47066,19,0.187852";

        MockMultipartFile file = new MockMultipartFile("file", "valid.csv", "text/csv", csvContent.getBytes());
        ClientResponse response = userDataSetServices.bulkUpload(new MockMultipartFile[]{file});
        verify(dataSetService, times(1)).saveAll(anyList());

    }

    @Test
    void bulkUpload_InvalidFileFormat() {
        MockMultipartFile invalidFile = new MockMultipartFile("file", "invalid.txt", "text/plain", "Invalid content".getBytes());
        ClientResponse response = userDataSetServices.bulkUpload(new MockMultipartFile[]{invalidFile});

    }

    @Test
    void bulkUpload_EmptyInput() {
        ClientResponse response = userDataSetServices.bulkUpload(new MockMultipartFile[]{});
        verifyNoInteractions(dataSetService);

    }
}