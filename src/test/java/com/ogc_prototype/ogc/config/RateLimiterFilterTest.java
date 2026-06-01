package com.ogc_prototype.ogc.config;

import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;
import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class RateLimiterFilterTest {

    private RateLimiterFilter filter;

    @Mock
    private FilterChain chain;

    @BeforeEach
    void setUp() {
        filter = new RateLimiterFilter(new ObjectMapper());
        // Small window for fast tests
        ReflectionTestUtils.setField(filter, "capacity", 3);
        ReflectionTestUtils.setField(filter, "windowSeconds", 1L);
    }

    @Test
    void requestUnderCapacity_passesThrough() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("10.0.0.1");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilterInternal(request, response, chain);

        verify(chain).doFilter(request, response);
        assertThat(response.getStatus()).isNotEqualTo(429);
    }

    @Test
    void requestsUpToCapacity_allPassThrough() throws Exception {
        String ip = "10.0.0.2";
        for (int i = 0; i < 3; i++) {
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setRemoteAddr(ip);
            filter.doFilterInternal(request, new MockHttpServletResponse(), chain);
        }

        verify(chain, times(3)).doFilter(org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any());
    }

    @Test
    void requestOverCapacity_returns429() throws Exception {
        String ip = "10.0.0.3";
        // Fill up to capacity
        for (int i = 0; i < 3; i++) {
            MockHttpServletRequest req = new MockHttpServletRequest();
            req.setRemoteAddr(ip);
            filter.doFilterInternal(req, new MockHttpServletResponse(), chain);
        }

        // Next request should be blocked
        MockHttpServletRequest blockedReq = new MockHttpServletRequest();
        blockedReq.setRemoteAddr(ip);
        MockHttpServletResponse blockedResp = new MockHttpServletResponse();

        filter.doFilterInternal(blockedReq, blockedResp, chain);

        assertThat(blockedResp.getStatus()).isEqualTo(429);
        assertThat(blockedResp.getContentAsString()).contains("Demasiadas peticiones");
    }

    @Test
    void differentIPs_haveIndependentLimits() throws Exception {
        String ip1 = "10.0.0.4";
        String ip2 = "10.0.0.5";

        // Fill ip1 to capacity
        for (int i = 0; i < 3; i++) {
            MockHttpServletRequest req = new MockHttpServletRequest();
            req.setRemoteAddr(ip1);
            filter.doFilterInternal(req, new MockHttpServletResponse(), chain);
        }

        // ip2 should still be allowed
        MockHttpServletRequest req2 = new MockHttpServletRequest();
        req2.setRemoteAddr(ip2);
        MockHttpServletResponse resp2 = new MockHttpServletResponse();
        filter.doFilterInternal(req2, resp2, chain);

        assertThat(resp2.getStatus()).isNotEqualTo(429);
    }

    @Test
    void afterWindowExpires_requestsAllowedAgain() throws Exception {
        String ip = "10.0.0.6";
        // Fill to capacity
        for (int i = 0; i < 3; i++) {
            MockHttpServletRequest req = new MockHttpServletRequest();
            req.setRemoteAddr(ip);
            filter.doFilterInternal(req, new MockHttpServletResponse(), chain);
        }

        // Wait for the 1-second window to expire
        Thread.sleep(1100);

        MockHttpServletRequest newReq = new MockHttpServletRequest();
        newReq.setRemoteAddr(ip);
        MockHttpServletResponse newResp = new MockHttpServletResponse();
        filter.doFilterInternal(newReq, newResp, chain);

        assertThat(newResp.getStatus()).isNotEqualTo(429);
    }

    @Test
    void xForwardedFor_header_usedAsClientIp() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Forwarded-For", "203.0.113.1");
        request.setRemoteAddr("192.168.0.1");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilterInternal(request, response, chain);

        verify(chain).doFilter(request, response);
    }

    @Test
    void cleanStaleEntries_doesNotThrow() {
        // Just verify the cleanup scheduled method can be invoked without exceptions
        filter.cleanStaleEntries();
    }
}
