/* JavaScript Document */
jQuery(document).ready(function() {
    'use strict';

	/*  Team Carousel = owl.carousel.js */
	jQuery('.team-carousel1').owlCarousel({
		loop:true,
		margin:30,
		nav:true,
		autoplaySpeed: 3000,
		navSpeed: 3000,
		paginationSpeed: 3000,
		slideSpeed: 3000,
		smartSpeed: 3000,
        autoplay: true,
		dots: true,
		navText: ['<i class="ti-arrow-left"></i>', '<i class="ti-arrow-right"></i>'],
		responsive:{
			0:{
				items:1
			},
			600:{
				items:1
			},			
			
			767:{
				items:2
			},
			1200:{
				items:3
			}
		}
	})
	
	/*  Team Carousel = owl.carousel.js */
	jQuery('.team-carousel2').owlCarousel({
		loop:true,
		margin:30,
		nav:true,
		autoplaySpeed: 3000,
		navSpeed: 3000,
		paginationSpeed: 3000,
		slideSpeed: 3000,
		smartSpeed: 3000,
        autoplay: true,
		dots: true,
		navText: ['<i class="ti-arrow-left"></i>', '<i class="ti-arrow-right"></i>'],
		responsive:{
			0:{
				items:1
			},
			600:{
				items:2
			},			
			
			767:{
				items:3
			},
			1200:{
				items:4
			}
		}
	})
	
	/*  Testimonials Carousel = owl.carousel.js */
	jQuery('.testimonials-carousel1').owlCarousel({
		loop:true,
		margin:30,
		nav:true,
		autoplaySpeed: 3000,
		navSpeed: 3000,
		paginationSpeed: 3000,
		slideSpeed: 3000,
		smartSpeed: 3000,
        autoplay: true,
		dots: false,
		navText: ['<i class="fa fa-arrow-left"></i>', '<i class="fa fa-arrow-right"></i>'],
		responsive:{
			0:{
				items:1
			},
			600:{
				items:1
			},			
			
			767:{
				items:2
			},
			1200:{
				items:3
			},
			2400:{
				items:4
			}
		}
	})
	
	/*  Testimonials Carousel = owl.carousel.js */
	jQuery('.testimonials-carousel2').owlCarousel({
		loop:true,
		margin:30,
		nav:true,
		autoplaySpeed: 3000,
		navSpeed: 3000,
		paginationSpeed: 3000,
		slideSpeed: 3000,
		smartSpeed: 3000,
        autoplay: true,
		dots: true,
		navText: ['<i class="fa fa-arrow-left"></i>', '<i class="fa fa-arrow-right"></i>'],
		responsive:{
			0:{
				items:1
			},
			600:{
				items:1
			},			
			
			767:{
				items:1
			},
			1200:{
				items:1
			}
		}
	})
		
	/*  Clients Carousel = owl.carousel.js */
	jQuery('.clients-carousel').owlCarousel({
		loop:true,
		margin:30,
		nav:true,
		autoplaySpeed: 3000,
		navSpeed: 3000,
		paginationSpeed: 3000,
		slideSpeed: 3000,
		smartSpeed: 3000,
        autoplay: true,
		dots: true,
		navText: ['<i class="fa fa-arrow-left"></i>', '<i class="fa fa-arrow-right"></i>'],
		responsive:{
			0:{
				items:2
			},
			600:{
				items:3
			},			
			
			767:{
				items:5
			},
			1200:{
				items:6
			}
		}
	})
	
	/*  Blgo Carousel = owl.carousel.js */
	jQuery('.blog-carousel1').owlCarousel({
		loop:true,
		margin:10,
		nav:true,
		autoplaySpeed: 3000,
		navSpeed: 3000,
		paginationSpeed: 3000,
		slideSpeed: 3000,
		smartSpeed: 3000,
        autoplay: true,
		dots: true,
		navText: ['<i class="fa fa-arrow-left"></i>', '<i class="fa fa-arrow-right"></i>'],
		responsive:{
			0:{
				items:1
			},
			600:{
				items:1
			},			
			
			767:{
				items:2
			},
			1200:{
				items:3
			}
		}
	})
	
	
});

/* Document .ready END */