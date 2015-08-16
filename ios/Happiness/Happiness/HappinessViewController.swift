//
//  HappinessViewController.swift
//  Happiness
//
//  Created by FranklinMichael on 15/08/15.
//  Copyright © 2015 Laminin. All rights reserved.
//

import UIKit

class HappinessViewController: UIViewController {

    var happiness: Int = 50 {
        didSet{
            happiness = min(max(happiness, 0), 100)
            print("Happiness = \(happiness)")
            updateUI()
        }
    }
    
    func updateUI(){
        
    }
}
