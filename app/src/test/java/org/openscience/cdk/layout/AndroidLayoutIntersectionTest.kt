package org.openscience.cdk.layout
import org.junit.Assert.*
import org.junit.Test
class AndroidLayoutIntersectionTest {
    @Test fun crossingsEndpointsCollinearAndSeparatedSegments(){
        assertTrue(LayoutRefiner.segmentsIntersect(0.0,0.0,2.0,2.0,0.0,2.0,2.0,0.0))
        assertTrue(LayoutRefiner.segmentsIntersect(0.0,0.0,1.0,1.0,1.0,1.0,2.0,0.0))
        assertTrue(LayoutRefiner.segmentsIntersect(0.0,0.0,4.0,0.0,1.0,0.0,3.0,0.0))
        assertFalse(LayoutRefiner.segmentsIntersect(0.0,0.0,1.0,0.0,2.0,0.0,3.0,0.0))
        assertFalse(LayoutRefiner.segmentsIntersect(0.0,0.0,1.0,0.0,0.0,1.0,1.0,1.0))
        assertTrue(LayoutRefiner.segmentsIntersect(1.0,0.0,1.0,0.0,0.0,0.0,2.0,0.0))
        assertFalse(LayoutRefiner.segmentsIntersect(1.0,1.0,1.0,1.0,0.0,0.0,2.0,0.0))
    }
}
